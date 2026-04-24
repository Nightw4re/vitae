package com.vitae.data;

import java.util.List;

/**
 * Type-specific parameters for an ability.
 *
 * <p>Only the relevant fields are used for a given ability type.
 * Existing built-in types use the numeric and entity-id fields; external spell bridges
 * can use {@code providerId} and {@code spellId}.
 */
public record AbilityParameters(
        double damage,
        double knockback,
        String projectileId,
        double speed,
        String summonId,
        int count,
        double radius,
        String effect,
        String providerId,
        String spellId,
        String entityId,
        String blockId,
        int durationTicks,
        boolean invulnerable,
        boolean noAi,
        boolean interruptible,
        double heightOffset,
        List<SpawnPointDefinition> spawnPoints,
        double scaleMultiplier
) {
    public static AbilityParameters empty() {
        return new AbilityParameters(0, 0, null, 1.0, null, 1, 0, null, null, null, null, null, 0, false, false, true, 0.6D, List.of(), 1.0D);
    }
}
