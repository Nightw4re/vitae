package com.vitae.data;

import java.util.List;

/**
 * Computes summon spawn offsets from explicit points or fallback patterns.
 */
public final class SpawnFormationUtil {

    private SpawnFormationUtil() {}

    public static SpawnPointDefinition resolvePoint(List<SpawnPointDefinition> spawnPoints, double radius, int count, int index) {
        if (spawnPoints != null && !spawnPoints.isEmpty()) {
            return spawnPoints.get(index % spawnPoints.size());
        }

        if (count == 4) {
            double offset = radius > 0.0D ? radius : 2.0D;
            return switch (index % 4) {
                case 0 -> SpawnPointDefinition.of(offset, offset);
                case 1 -> SpawnPointDefinition.of(-offset, offset);
                case 2 -> SpawnPointDefinition.of(-offset, -offset);
                default -> SpawnPointDefinition.of(offset, -offset);
            };
        }

        double angle = (2 * Math.PI / Math.max(1, count)) * index;
        return SpawnPointDefinition.of(Math.cos(angle) * radius, Math.sin(angle) * radius);
    }
}
