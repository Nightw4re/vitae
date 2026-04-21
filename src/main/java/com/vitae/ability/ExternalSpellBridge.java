package com.vitae.ability;

import com.vitae.data.AbilityDefinition;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

/**
 * Optional adapter implemented by compatibility modules for external spell mods.
 *
 * <p>Examples: Iron's Spells, Ars Nouveau, or any other mod that wants to expose
 * its spells as Vitae abilities without adding a hard dependency to Vitae core.
 */
public interface ExternalSpellBridge {
    String providerId();

    void cast(Mob caster, LivingEntity target, AbilityDefinition ability);
}
