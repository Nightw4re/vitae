package com.vitae.data;

/**
 * Type-specific parameters for an ability.
 *
 * <p>Fields are nullable — only the relevant ones for a given ability type are used.
 *
 * <ul>
 *   <li>{@code melee_attack} — uses {@code damage}, {@code knockback}
 *   <li>{@code ranged_projectile} — uses {@code projectileId}, {@code speed}, {@code damage}
 *   <li>{@code summon} — uses {@code summonId}, {@code count}, {@code radius}
 *   <li>{@code aoe} — uses {@code radius}, {@code damage}, {@code effect}
 *   <li>{@code dash} — uses {@code speed}, {@code damage}
 * </ul>
 *
 * @param damage       damage dealt (ability-type dependent meaning)
 * @param knockback    knockback strength for melee
 * @param projectileId resource location of the projectile entity
 * @param speed        projectile or dash speed multiplier
 * @param summonId     resource location of the entity to summon
 * @param count        number of entities to summon
 * @param radius       radius in blocks for summon scatter or aoe
 * @param effect       potion effect ID to apply on aoe hit (nullable)
 */
public record AbilityParameters(
        double damage,
        double knockback,
        String projectileId,
        double speed,
        String summonId,
        int count,
        double radius,
        String effect
) {
    public static AbilityParameters empty() {
        return new AbilityParameters(0, 0, null, 1.0, null, 1, 0, null);
    }
}
