package com.vitae.data;

/**
 * Configures baseline combat behavior for a Vitae entity.
 *
 * @param basicMeleeEnabled      whether the entity can perform its default melee attack
 * @param basicMeleeCooldownTicks cooldown between melee hits in ticks
 * @param basicMeleeRange        melee hit range in blocks
 * @param scaleDamageWithHeldWeapon whether main-hand weapon modifiers affect melee damage
 * @param scaleAttackSpeedWithHeldWeapon whether main-hand weapon modifiers affect melee cadence
 * @param spinRadius             radius for spin ability AoE
 * @param spinInvulnerable       whether the entity becomes invulnerable while spinning
 */
public record CombatDefinition(
        boolean basicMeleeEnabled,
        int basicMeleeCooldownTicks,
        double basicMeleeRange,
        boolean scaleDamageWithHeldWeapon,
        boolean scaleAttackSpeedWithHeldWeapon,
        double spinRadius,
        boolean spinInvulnerable
) {
    public static CombatDefinition defaults() {
        return new CombatDefinition(true, 20, 3.0, false, false, 3.0, true);
    }
}
