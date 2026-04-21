package com.vitae.data;

import java.util.List;

/**
 * Optional natural spawn restrictions for a Vitae entity.
 */
public record SpawnRules(
        List<String> biomes,
        List<String> structures,
        int maxNearbyGlobal,
        int maxNearbyPerBiome,
        int maxNearbyPerStructure
) {
    public static SpawnRules defaults() {
        return new SpawnRules(List.of(), List.of(), 0, 0, 0);
    }

    public List<String> biomesOrDefault() {
        return biomes != null ? biomes : List.of();
    }

    public List<String> structuresOrDefault() {
        return structures != null ? structures : List.of();
    }

    public boolean hasAnyRestrictions() {
        return !biomesOrDefault().isEmpty() || !structuresOrDefault().isEmpty()
                || maxNearbyGlobal() > 0 || maxNearbyPerBiome() > 0 || maxNearbyPerStructure() > 0;
    }
}
