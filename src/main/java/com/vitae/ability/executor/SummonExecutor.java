package com.vitae.ability.executor;

import com.vitae.ability.AbilityExecutor;
import com.vitae.data.AbilityDefinition;
import com.vitae.data.SpawnFormationUtil;
import com.vitae.data.SpawnPointDefinition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

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
        var spawnPoints = ability.parameters().spawnPoints();
        double scaleMultiplier = ability.parameters().scaleMultiplier();

        if (summonId == null || summonId.isBlank()) return;

        Optional<EntityType<?>> entityType = EntityType.byString(summonId);
        if (entityType.isEmpty()) return;

        for (int i = 0; i < count; i++) {
            SpawnPointDefinition point = SpawnFormationUtil.resolvePoint(spawnPoints, radius, count, i);
            var spawned = entityType.get().create(level);
            if (spawned == null) {
                continue;
            }
            spawned.moveTo(caster.getX() + point.x(), caster.getY() + point.y(), caster.getZ() + point.z(), spawned.getYRot(), spawned.getXRot());
            level.addFreshEntity(spawned);
            if (spawned instanceof LivingEntity living && scaleMultiplier != 1.0D) {
                applyScale(living, scaleMultiplier);
            }
        }
    }

    private void applyScale(LivingEntity living, double scaleMultiplier) {
        try {
            living.getClass().getMethod("setScale", float.class).invoke(living, (float) scaleMultiplier);
        } catch (ReflectiveOperationException ignored) {
            // Optional visual-only scaling hook; harmless if unsupported.
        }
    }
}
