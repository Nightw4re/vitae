package com.vitae.data;

/**
 * Defines a single ability that can be used by a Vitae entity.
 *
 * <p>Abilities are referenced by ID from {@link PhaseDefinition#abilities()}.
 * Built-in types: {@code melee_attack}, {@code ranged_projectile}, {@code summon},
 * {@code aoe}, {@code dash}, {@code external_spell}, {@code sequence}.
 *
 * @param id            unique identifier for this ability
 * @param type          built-in ability type
 * @param cooldownTicks ticks between uses
 * @param condition     activation conditions
 * @param parameters    type-specific parameters (projectile id, summon entity, aoe radius, etc.)
 * @param steps         optional child ability steps for sequence abilities
 * @param randomDelayTicksMin minimum random delay before cast start
 * @param randomDelayTicksMax maximum random delay before cast start
 * @param interruptible whether another ability may interrupt the cast
 */
public record AbilityDefinition(
        String id,
        String type,
        int cooldownTicks,
        AbilityCondition condition,
        AbilityParameters parameters,
        java.util.List<AbilityStepDefinition> steps,
        int randomDelayTicksMin,
        int randomDelayTicksMax,
        boolean interruptible
) {}
