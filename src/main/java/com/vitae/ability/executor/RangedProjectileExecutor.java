package com.vitae.ability.executor;

import com.vitae.ability.AbilityExecutor;
import com.vitae.data.AbilityDefinition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

/**
 * Fires an arrow toward the target with custom damage and speed.
 */
public final class RangedProjectileExecutor implements AbilityExecutor {

    @Override
    public void execute(Mob caster, LivingEntity target, AbilityDefinition ability) {
        if (target == null || caster.level().isClientSide()) return;

        ServerLevel level = (ServerLevel) caster.level();
        double speed  = ability.parameters().speed();
        double damage = ability.parameters().damage();

        Arrow arrow = new Arrow(level, caster, new ItemStack(Items.ARROW), null);
        Vec3 dir = target.getEyePosition().subtract(caster.getEyePosition()).normalize();
        arrow.shoot(dir.x, dir.y, dir.z, (float) speed, 1.0f);
        arrow.setBaseDamage(damage);
        arrow.pickup = AbstractArrow.Pickup.DISALLOWED;

        level.addFreshEntity(arrow);
    }
}
