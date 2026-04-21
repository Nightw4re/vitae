package com.vitae.ability.executor;

import com.vitae.ability.AbilityExecutor;
import com.vitae.data.AbilityDefinition;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

public final class LevitateTargetExecutor implements AbilityExecutor {
    @Override
    public void execute(Mob caster, LivingEntity target, AbilityDefinition ability) {
        if (target == null || caster.level().isClientSide()) {
            return;
        }
        int duration = ability.parameters() != null ? Math.max(0, ability.parameters().durationTicks()) : 0;
        double lift = ability.parameters() != null ? Math.max(0.0D, ability.parameters().heightOffset()) : 0.6D;
        if (duration > 0) {
            target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, duration, 0, false, false, true));
        } else {
            target.setDeltaMovement(new Vec3(target.getDeltaMovement().x, lift, target.getDeltaMovement().z));
            target.hurtMarked = true;
        }
    }
}
