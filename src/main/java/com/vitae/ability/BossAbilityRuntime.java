package com.vitae.ability;

import com.vitae.data.AbilityDefinition;
import com.vitae.data.AbilityParameters;
import com.vitae.data.EntityDefinition;
import com.vitae.data.AbilityReference;
import java.util.List;
import com.vitae.effect.VitaeEffectHooks;
import net.minecraft.world.entity.Mob;

public final class BossAbilityRuntime {

    private static final int SPIN_DURATION_TICKS = 200;
    private static final int SPIN_COOLDOWN_TICKS = 600;
    private static final int SPIN_PULSE_INTERVAL_TICKS = 40;

    private int spinCooldown;
    private int spinTicksRemaining;
    private int spinPulseCooldown;
    private boolean invulnerableApplied;

    public void tick(Mob caster, EntityDefinition definition) {
        if (caster.level().isClientSide()) {
            return;
        }

        if (spinCooldown > 0) {
            spinCooldown--;
        }
        if (shouldStartSpin(caster, definition)) {
            startSpin(caster, definition);
        }
        tickSpin(caster, definition);
    }

    public boolean isSpinActive() {
        return spinTicksRemaining > 0;
    }

    private boolean shouldStartSpin(Mob caster, EntityDefinition definition) {
        if (spinTicksRemaining > 0 || spinCooldown > 0) return false;
        if (caster.getHealth() > caster.getMaxHealth() * 0.5F) return false;
        return hasSpinAbility(definition, caster.getHealth() / caster.getMaxHealth());
    }

    private void startSpin(Mob caster, EntityDefinition definition) {
        spinTicksRemaining = SPIN_DURATION_TICKS;
        spinPulseCooldown = 0;
        spinCooldown = SPIN_COOLDOWN_TICKS;
        caster.getNavigation().stop();
        caster.setAggressive(false);
        if (definition.combatOrDefault().spinInvulnerable()) {
            caster.setInvulnerable(true);
            invulnerableApplied = true;
        }
        VitaeEffectHooks.playSpinStart(caster.level(), caster);
    }

    private void tickSpin(Mob caster, EntityDefinition definition) {
        if (spinTicksRemaining <= 0) {
            return;
        }

        spinTicksRemaining--;
        spinPulseCooldown--;
        caster.setYRot(caster.getYRot() + 25.0F);
        caster.yBodyRot = caster.getYRot();
        caster.yHeadRot = caster.getYRot();
        caster.setDeltaMovement(0.0D, caster.getDeltaMovement().y, 0.0D);
        caster.getNavigation().stop();

        if (spinPulseCooldown <= 0) {
            spinPulseCooldown = SPIN_PULSE_INTERVAL_TICKS;
            AbilityExecutor executor = AbilityExecutorRegistry.get().getExecutor("spin_slash");
            if (executor != null) {
                AbilityParameters parameters = new AbilityParameters(
                        definition.attributes().attackDamage(),
                        0.0D,
                        null,
                        1.0D,
                        null,
                        1,
                        definition.combatOrDefault().spinRadius(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        0,
                        false,
                        false,
                        true,
                        0.6D,
                        null,
                        1.0D
                );
                executor.execute(caster, caster.getTarget(), new AbilityDefinition(
                        "spin_slash",
                        "spin_slash",
                        SPIN_PULSE_INTERVAL_TICKS,
                        null,
                        parameters,
                        java.util.List.of(),
                        0,
                        0,
                        true
                ));
            } else {
                VitaeEffectHooks.playSpinPulse(caster.level(), caster);
            }
        }

        if (spinTicksRemaining <= 0) {
            if (invulnerableApplied) {
                caster.setInvulnerable(false);
                invulnerableApplied = false;
            }
            caster.setAggressive(caster.getTarget() != null && caster.getTarget().isAlive());
        }
    }
    private boolean hasSpinAbility(EntityDefinition definition, double healthPercent) {
        var phase = definition.getPhaseForHealth(healthPercent);
        return phase != null && containsAbilityId(phase.abilities(), "spin_slash");
    }

    private boolean containsAbilityId(List<AbilityReference> abilities, String abilityId) {
        if (abilities == null || abilityId == null) {
            return false;
        }
        String namespaced = "vitae:" + abilityId;
        for (AbilityReference reference : abilities) {
            if (reference == null || reference.id() == null) {
                continue;
            }
            if (abilityId.equals(reference.id()) || namespaced.equals(reference.id())) {
                return true;
            }
        }
        return false;
    }
}
