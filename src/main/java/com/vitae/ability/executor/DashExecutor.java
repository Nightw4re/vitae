package com.vitae.ability.executor;

import com.vitae.ability.AbilityExecutor;
import com.vitae.data.AbilityDefinition;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/**
 * Launches the caster toward its target at high speed, dealing damage on arrival.
 *
 * <p>Parameters: {@code speed} (velocity multiplier), {@code damage} (on hit).
 */
public final class DashExecutor implements AbilityExecutor {

    @Override
    public void execute(Mob caster, LivingEntity target, AbilityDefinition ability) {
        if (target == null) return;

        double speed  = ability.parameters().speed();
        double damage = ability.parameters().damage();

        Vec3 direction = target.position().subtract(caster.position()).normalize();
        caster.setDeltaMovement(direction.scale(speed));
        caster.hurtMarked = true;

        // damage target if already in contact range
        if (caster.distanceTo(target) <= 2.5) {
            target.hurt(caster.damageSources().mobAttack(caster), (float) damage);
        }
    }
}
