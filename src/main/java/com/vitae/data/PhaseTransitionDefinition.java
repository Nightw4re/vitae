package com.vitae.data;

/**
 * Defines what happens when the boss transitions into a new phase.
 *
 * @param animation      animation to play during the transition (plays once)
 * @param invulnerable   whether the boss cannot take damage during the transition
 * @param durationTicks  how long the transition lasts in ticks; -1 means use animation length
 */
public record PhaseTransitionDefinition(
        String animation,
        boolean invulnerable,
        int durationTicks
) {
    public static final int USE_ANIMATION_LENGTH = -1;
}
