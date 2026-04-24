package com.vitae.phase;

import com.vitae.data.EntityDefinition;
import com.vitae.data.AbilityDefinition;
import com.vitae.data.PhaseDefinition;
import com.vitae.data.PhaseLockDefinition;
import com.vitae.registry.VitaeRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Keeps a boss locked at a health floor while its summoned support units are alive.
 */
public final class PhaseLockController {

    private String activePhaseId;
    private final Set<UUID> trackedSummons = new HashSet<>();
    private boolean invulnerableApplied;
    private boolean summonLockActive;

    public void tick(Mob caster, EntityDefinition definition) {
        if (caster.level().isClientSide()) {
            return;
        }

        PhaseDefinition phase = definition.getPhaseForHealth(caster.getHealth() / caster.getMaxHealth());
        if (phase == null) {
            clearLock(caster);
            activePhaseId = null;
            return;
        }

        PhaseLockDefinition lock = phase.lock();
        AbilityDefinition summonAbility = resolveRootSummonAbility(definition);
        boolean hpLockPhase = definition.hasHpLockThreshold(phase.healthThreshold());
        if (!phase.id().equals(activePhaseId)) {
            clearLock(caster);
            activePhaseId = phase.id();
            if (lock != null || (hpLockPhase && summonAbility != null)) {
                startLock(caster, phase, lock, summonAbility);
            }
        }

        if (lock == null && !hpLockPhase) {
            if (invulnerableApplied) {
                caster.setInvulnerable(false);
                invulnerableApplied = false;
            }
            summonLockActive = false;
            return;
        }

        if (!trackedSummons.isEmpty()) {
            trackedSummons.removeIf(uuid -> !isAlive(getEntity(caster, uuid)));
        }

        if (!trackedSummons.isEmpty()) {
            enforceHealthFloor(caster, phase.healthFloorPercent());
        }

        boolean invulnerableWhileSummonsAlive = lock != null ? lock.invulnerableWhileSummonsAlive() : summonAbility != null && summonAbility.parameters().invulnerable();
        if (invulnerableWhileSummonsAlive && !trackedSummons.isEmpty()) {
            if (!invulnerableApplied) {
                caster.setInvulnerable(true);
                invulnerableApplied = true;
            }
            summonLockActive = true;
        } else if (invulnerableApplied) {
            caster.setInvulnerable(false);
            invulnerableApplied = false;
            summonLockActive = false;
        } else if (trackedSummons.isEmpty()) {
            summonLockActive = false;
        }
    }

    public void reset(Mob caster) {
        clearLock(caster);
        activePhaseId = null;
    }

    public boolean isLocked() {
        return activePhaseId != null && !trackedSummons.isEmpty();
    }

    public boolean isPhaseLockActive() {
        return activePhaseId != null;
    }

    public double currentPhaseLockFloorOrDefault(EntityDefinition definition, Mob caster) {
        if (definition == null || activePhaseId == null || caster == null) {
            return 0.0D;
        }
        PhaseDefinition phase = definition.getPhaseForHealth(caster.getHealth() / caster.getMaxHealth());
        if (phase == null || !phase.id().equals(activePhaseId) || phase.lock() == null || !summonLockActive) {
            return 0.0D;
        }
        return Math.max(0.0D, phase.healthFloorPercent());
    }

    public boolean isSummonLockActive() {
        return summonLockActive;
    }

    private void startLock(Mob caster, PhaseDefinition phase, PhaseLockDefinition lock, AbilityDefinition summonAbility) {
        if (lock == null && summonAbility == null) {
            return;
        }
        if (phase != null && phase.hasHealthFloor()) {
            enforceHealthFloor(caster, phase.healthFloorPercent());
        }
        if (lock != null && lock.summonCount() > 0 && lock.summonEntity() != null && !lock.summonEntity().isBlank()) {
            spawnSummons(caster, lock.summonEntity(), lock.summonCount());
        } else if (summonAbility != null && summonAbility.parameters() != null) {
            String summonId = summonAbility.parameters().summonId();
            int count = Math.max(1, summonAbility.parameters().count());
            if (summonId != null && !summonId.isBlank()) {
                spawnSummons(caster, summonAbility, summonId, count);
            }
        }
        boolean invulnerableWhileSummonsAlive = lock != null ? lock.invulnerableWhileSummonsAlive() : summonAbility != null && summonAbility.parameters().invulnerable();
        if (invulnerableWhileSummonsAlive) {
            caster.setInvulnerable(true);
            invulnerableApplied = true;
        }
    }

    private void spawnSummons(Mob caster, String summonEntityId, int summonCount) {
        spawnSummons(caster, null, summonEntityId, summonCount);
    }

    private void spawnSummons(Mob caster, AbilityDefinition ability, String summonEntityId, int summonCount) {
        if (!(caster.level() instanceof ServerLevel level)) {
            return;
        }
        EntityType.byString(summonEntityId).ifPresent(type -> {
            var parameters = ability != null ? ability.parameters() : null;
            for (int i = 0; i < summonCount; i++) {
                var point = parameters != null
                        ? com.vitae.data.SpawnFormationUtil.resolvePoint(parameters.spawnPoints(), parameters.radius(), summonCount, i)
                        : com.vitae.data.SpawnPointDefinition.of(0.0D, 0.0D);
                Entity spawned = type.create(level);
                if (spawned == null) {
                    continue;
                }
                spawned.moveTo(caster.getX() + point.x(), caster.getY() + point.y(), caster.getZ() + point.z(), spawned.getYRot(), spawned.getXRot());
                level.addFreshEntity(spawned);
                if (spawned instanceof Mob mob) {
                    mob.setPersistenceRequired();
                    trackedSummons.add(mob.getUUID());
                } else {
                    trackedSummons.add(spawned.getUUID());
                }
                if (parameters != null && parameters.scaleMultiplier() != 1.0D && spawned instanceof net.minecraft.world.entity.LivingEntity living) {
                    try {
                        living.getClass().getMethod("setScale", float.class).invoke(living, (float) parameters.scaleMultiplier());
                    } catch (ReflectiveOperationException ignored) {
                    }
                }
            }
        });
    }

    private void enforceHealthFloor(Mob caster, double healthFloorPercent) {
        float maxHealth = caster.getMaxHealth();
        if (maxHealth <= 0.0F) {
            return;
        }
        float minimumHealth = Math.max(1.0F, maxHealth * (float) Math.max(0.0D, healthFloorPercent));
        if (caster.getHealth() < minimumHealth) {
            caster.setHealth(minimumHealth);
        }
    }

    private AbilityDefinition resolveRootSummonAbility(EntityDefinition definition) {
        if (definition == null) {
            return null;
        }
        if (definition.getAbility("vitae:guard_summon") != null) {
            AbilityDefinition loaded = VitaeRegistry.get().getAbility(ResourceLocation.fromNamespaceAndPath("vitae", "guard_summon"));
            if (loaded != null) {
                return loaded;
            }
        }
        if (definition.getAbility("guard_summon") != null) {
            AbilityDefinition loaded = VitaeRegistry.get().getAbility(ResourceLocation.fromNamespaceAndPath("vitae", "guard_summon"));
            if (loaded != null) {
                return loaded;
            }
        }
        return null;
    }

    private void clearLock(Mob caster) {
        trackedSummons.clear();
        if (invulnerableApplied) {
            caster.setInvulnerable(false);
            invulnerableApplied = false;
        }
        summonLockActive = false;
    }

    private boolean isAlive(Entity entity) {
        return entity != null && entity.isAlive() && !entity.isRemoved();
    }

    private Entity getEntity(Mob caster, UUID uuid) {
        if (!(caster.level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        return serverLevel.getEntity(uuid);
    }
}
