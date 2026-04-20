package com.vitae.phase;

import com.vitae.data.EntityDefinition;
import com.vitae.data.PhaseDefinition;

/**
 * Tracks the current boss phase and detects phase transitions.
 *
 * <p>Pure Java — no Minecraft dependency. Designed to be owned by a {@code VitaeMob} instance
 * and ticked each game tick.
 */
public final class PhaseManager {

    private final EntityDefinition definition;
    private PhaseDefinition currentPhase;
    private boolean inTransition;

    public PhaseManager(EntityDefinition definition) {
        this.definition = definition;
        this.currentPhase = definition.getPhaseForHealth(1.0);
        this.inTransition = false;
    }

    /**
     * Updates the phase based on the current health percentage.
     *
     * @param healthPercent current health as a fraction of max health (0.0–1.0)
     * @return a {@link PhaseTransitionResult} describing what changed, or {@code null} if no change
     */
    public PhaseTransitionResult update(double healthPercent) {
        if (inTransition) return null;

        PhaseDefinition target = definition.getPhaseForHealth(healthPercent);
        if (target == null || target.equals(currentPhase)) return null;

        PhaseDefinition previous = currentPhase;
        currentPhase = target;

        if (target.hasTransition()) {
            inTransition = true;
        }

        return new PhaseTransitionResult(previous, target);
    }

    /** Signals that the transition animation has finished. */
    public void completeTransition() {
        inTransition = false;
    }

    /** Resets the phase manager back to phase 1 (full health). */
    public void reset() {
        currentPhase = definition.getPhaseForHealth(1.0);
        inTransition = false;
    }

    public PhaseDefinition getCurrentPhase() {
        return currentPhase;
    }

    public boolean isInTransition() {
        return inTransition;
    }
}
