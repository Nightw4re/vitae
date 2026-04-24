package com.vitae.data;

/**
 * Relative spawn offset for summon abilities.
 *
 * @param x horizontal X offset from the caster
 * @param z horizontal Z offset from the caster
 * @param y vertical offset from the caster
 */
public record SpawnPointDefinition(
        double x,
        double z,
        double y
) {
    public static SpawnPointDefinition of(double x, double z) {
        return new SpawnPointDefinition(x, z, 0.0D);
    }
}
