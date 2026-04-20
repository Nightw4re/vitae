package com.vitae.ability;

import com.vitae.data.AbilityDefinition;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

/**
 * Executes the effect of a single ability on a caster targeting a living entity.
 *
 * <p>One executor per ability type. Registered in {@link AbilityExecutorRegistry}.
 */
@FunctionalInterface
public interface AbilityExecutor {

    /**
     * Performs the ability effect.
     *
     * @param caster  the Vitae entity using the ability
     * @param target  the current attack target (may be null for AoE/self-targeted abilities)
     * @param ability the ability definition with parameters
     */
    void execute(Mob caster, LivingEntity target, AbilityDefinition ability);
}
