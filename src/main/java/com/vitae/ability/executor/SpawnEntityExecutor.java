package com.vitae.ability.executor;

import com.vitae.ability.AbilityExecutor;
import com.vitae.data.AbilityDefinition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public final class SpawnEntityExecutor implements AbilityExecutor {
    private static final java.util.Map<java.util.UUID, java.util.UUID> LAST_SPAWNED = new java.util.concurrent.ConcurrentHashMap<>();

    @Override
    public void execute(Mob caster, LivingEntity target, AbilityDefinition ability) {
        if (caster.level().isClientSide()) {
            return;
        }
        String entityId = ability.parameters() != null ? ability.parameters().entityId() : null;
        if (entityId == null || entityId.isBlank()) {
            return;
        }
        Optional<EntityType<?>> entityType = EntityType.byString(entityId);
        if (entityType.isEmpty() || !(caster.level() instanceof ServerLevel level)) {
            return;
        }
        LivingEntity spawnTarget = target != null ? target : caster;
        Entity spawned = entityType.get().spawn(level, null, spawnTarget.blockPosition(), MobSpawnType.MOB_SUMMONED, false, false);
        if (spawned instanceof LivingEntity living) {
            living.setNoGravity(true);
            if (living.getAttribute(Attributes.MAX_HEALTH) != null) {
                living.getAttribute(Attributes.MAX_HEALTH).setBaseValue(4.0D);
                living.setHealth(4.0F);
            }
            if (ability.parameters() != null && ability.parameters().noAi() && living instanceof Mob mob) {
                mob.setNoAi(true);
            }
            if (living instanceof Mob mob) {
                mob.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                mob.setItemInHand(net.minecraft.world.InteractionHand.OFF_HAND, ItemStack.EMPTY);
            }
            LAST_SPAWNED.put(caster.getUUID(), living.getUUID());
        }
    }

    public static java.util.UUID getLastSpawned(Mob caster) {
        return LAST_SPAWNED.get(caster.getUUID());
    }
}
