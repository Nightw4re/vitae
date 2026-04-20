package com.vitae.data;

/**
 * Defines a single ability that can be used by a Vitae entity.
 *
 * <p>Abilities are referenced by ID from {@link PhaseDefinition#abilities()}.
 * Built-in types: {@code melee_attack}, {@code ranged_projectile}, {@code summon},
 * {@code aoe}, {@code dash}.
 *
 * @param id            unique identifier for this ability
 * @param type          built-in ability type
 * @param cooldownTicks ticks between uses
 * @param condition     activation conditions
 * @param parameters    type-specific parameters (projectile id, summon entity, aoe radius, etc.)
 */
public record AbilityDefinition(
        String id,
        String type,
        int cooldownTicks,
        AbilityCondition condition,
        AbilityParameters parameters
) {}
