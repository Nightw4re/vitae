package com.vitae.ability.executor;

import com.vitae.ability.AbilityExecutor;
import com.vitae.data.AbilityDefinition;
import com.vitae.effect.VitaeEffectHooks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public final class SpinSlashExecutor implements AbilityExecutor {

    @Override
    public void execute(Mob caster, LivingEntity target, AbilityDefinition ability) {
        if (caster.level().isClientSide()) {
            return;
        }

        double radius = ability.parameters() != null ? Math.max(0.5D, ability.parameters().radius()) : 3.0D;
        double damageMultiplier = ability.parameters() != null ? Math.max(0.1D, ability.parameters().damage()) : 1.25D;

        caster.setYRot(caster.getYRot() + 25.0F);
        caster.yBodyRot = caster.getYRot();
        caster.yHeadRot = caster.getYRot();

        VitaeEffectHooks.playSpinPulse(caster.level(), caster);

        AABB area = caster.getBoundingBox().inflate(radius);
        for (LivingEntity entity : caster.level().getEntitiesOfClass(LivingEntity.class, area, candidate -> candidate != caster && candidate.isAlive())) {
            if (!isValidTarget(caster, entity)) {
                continue;
            }
            entity.hurt(caster.damageSources().mobAttack(caster), (float) (caster.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE) * damageMultiplier));
        }
    }

    private boolean isValidTarget(Mob caster, LivingEntity target) {
        if (target.getType() == caster.getType()) {
            return false;
        }
        if (target instanceof Player player) {
            return !player.isCreative() && !player.isSpectator();
        }
        return target instanceof Mob;
    }
}
