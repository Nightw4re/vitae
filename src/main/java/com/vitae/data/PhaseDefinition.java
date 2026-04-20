package com.vitae.data;

import java.util.List;

/**
 * Defines a single boss phase — active abilities, health threshold, and visual overrides.
 *
 * <p>{@code healthThreshold} is a value between 0.0 and 1.0 representing the fraction of max health
 * at which this phase becomes active. The phase with the smallest threshold that is still
 * {@code >= currentHealthPercent} is the active one.
 *
 * @param id              unique identifier for this phase
 * @param healthThreshold fraction of max health at which this phase activates (0.0–1.0)
 * @param abilities       list of ability IDs active during this phase
 * @param animation       idle/loop animation name for this phase (nullable = use default)
 * @param model           optional GeckoLib model override for this phase (nullable = use entity default)
 * @param scale           optional scale multiplier for this phase (1.0 = normal size)
 * @param transition      optional transition definition played when entering this phase
 */
public record PhaseDefinition(
        String id,
        double healthThreshold,
        List<String> abilities,
        String animation,
        String model,
        double scale,
        PhaseTransitionDefinition transition
) {
    public static final double DEFAULT_SCALE = 1.0;

    /** Returns true if this phase overrides the entity's default model. */
    public boolean hasModelOverride() {
        return model != null && !model.isBlank();
    }

    /** Returns true if this phase has a transition animation configured. */
    public boolean hasTransition() {
        return transition != null;
    }
}
