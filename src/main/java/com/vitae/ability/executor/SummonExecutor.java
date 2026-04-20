package com.vitae.ability.executor;

import com.vitae.ability.AbilityExecutor;
import com.vitae.data.AbilityDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/**
 * Summons one or more entities scattered around the caster.
 *
 * <p>Requires {@code summon_id} (entity resource location), {@code count}, and {@code radius}
 * in ability parameters.
 */
public final class SummonExecutor implements AbilityExecutor {

    @Override
    public void execute(Mob caster, LivingEntity target, AbilityDefinition ability) {
        if (caster.level().isClientSide()) return;

        ServerLevel level = (ServerLevel) caster.level();
        String summonId = ability.parameters().summonId();
        int count       = Math.max(1, ability.parameters().count());
        double radius   = ability.parameters().radius();

        if (summonId == null || summonId.isBlank()) return;

        Optional<EntityType<?>> entityType = EntityType.byString(summonId);
        if (entityType.isEmpty()) return;

        for (int i = 0; i < count; i++) {
            double angle = (2 * Math.PI / count) * i;
            double x = caster.getX() + Math.cos(angle) * radius;
            double z = caster.getZ() + Math.sin(angle) * radius;
            entityType.get().spawn(level, null, caster.blockPosition().offset((int)(x - caster.getX()), 0, (int)(z - caster.getZ())), MobSpawnType.MOB_SUMMONED, false, false);
        }
    }
}
