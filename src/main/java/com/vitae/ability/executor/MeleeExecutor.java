package com.vitae.ability.executor;

import com.vitae.ability.AbilityExecutor;
import com.vitae.data.AbilityDefinition;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/**
 * Executes a melee attack with custom damage and knockback.
 */
public final class MeleeExecutor implements AbilityExecutor {

    @Override
    public void execute(Mob caster, LivingEntity target, AbilityDefinition ability) {
        if (target == null) return;

        double damage    = ability.parameters().damage();
        double knockback = ability.parameters().knockback();

        target.hurt(caster.damageSources().mobAttack(caster), (float) damage);

        if (knockback > 0) {
            Vec3 direction = target.position().subtract(caster.position()).normalize();
            target.knockback(knockback, -direction.x, -direction.z);
        }
    }
}
