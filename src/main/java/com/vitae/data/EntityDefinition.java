package com.vitae.data;

import java.util.Comparator;
import java.util.List;

/**
 * Immutable data model for a Vitae entity loaded from a datapack JSON file.
 *
 * <p>An entity definition lives at {@code data/<namespace>/vitae/entities/<name>.json}.
 */
public record EntityDefinition(
        String model,
        String animations,
        AttributeDefinition attributes,
        List<PhaseDefinition> phases,
        String lootTable,
        String introAnimation,
        DeathBehavior deathBehavior,
        ResetBehavior resetBehavior,
        BossBarDefinition bossBar,
        String spawnStructure
) {

    /**
     * Returns the active phase for the given health percentage (0.0–1.0).
     *
     * <p>Selects the phase with the smallest {@code healthThreshold} that is still
     * {@code >= healthPercent}. Falls back to the last phase if none qualifies.
     */
    public PhaseDefinition getPhaseForHealth(double healthPercent) {
        if (phases == null || phases.isEmpty()) {
            return null;
        }
        return phases.stream()
                .filter(p -> p.healthThreshold() >= healthPercent)
                .min(Comparator.comparingDouble(PhaseDefinition::healthThreshold))
                .orElse(phases.get(phases.size() - 1));
    }

    /** Returns true if this entity has a multi-phase boss setup. */
    public boolean isBoss() {
        return phases != null && phases.size() > 1;
    }

    /** Returns true if this entity has an intro animation. */
    public boolean hasIntro() {
        return introAnimation != null && !introAnimation.isBlank();
    }

    /** Returns true if this entity has a boss bar configured. */
    public boolean hasBossBar() {
        return bossBar != null;
    }

    /** Returns true if this entity is tied to a specific spawn structure. */
    public boolean hasSpawnStructure() {
        return spawnStructure != null && !spawnStructure.isBlank();
    }
}
