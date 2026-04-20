package com.vitae.ability.executor;

import com.vitae.ability.AbilityExecutor;
import com.vitae.data.AbilityDefinition;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Optional;

/**
 * Deals damage and optionally applies a potion effect to all living entities
 * within a radius around the caster.
 */
public final class AoeExecutor implements AbilityExecutor {

    private static final int EFFECT_DURATION_TICKS = 100;
    private static final int EFFECT_AMPLIFIER = 0;

    @Override
    public void execute(Mob caster, LivingEntity target, AbilityDefinition ability) {
        if (caster.level().isClientSide()) return;

        double radius   = ability.parameters().radius();
        double damage   = ability.parameters().damage();
        String effectId = ability.parameters().effect();

        AABB box = caster.getBoundingBox().inflate(radius);
        List<LivingEntity> nearby = caster.level().getEntitiesOfClass(LivingEntity.class, box,
                e -> e != caster && caster.hasLineOfSight(e));

        for (LivingEntity entity : nearby) {
            entity.hurt(caster.damageSources().mobAttack(caster), (float) damage);

            if (effectId != null && !effectId.isBlank()) {
                Optional<Holder.Reference<MobEffect>> effect = BuiltInRegistries.MOB_EFFECT
                        .getHolder(ResourceLocation.parse(effectId));
                effect.ifPresent(e -> entity.addEffect(
                        new MobEffectInstance(e, EFFECT_DURATION_TICKS, EFFECT_AMPLIFIER)));
            }
        }
    }
}
