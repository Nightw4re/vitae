package com.vitae.data;

/**
 * Controls what happens when a Vitae boss loses aggro (all players flee or die).
 *
 * @param fullHeal      whether to restore the boss to full health
 * @param returnToSpawn whether to teleport back to the spawn position
 * @param animation     animation to play while resetting (nullable)
 * @param clearPhases   whether to reset back to phase 1
 */
public record ResetBehavior(
        boolean fullHeal,
        boolean returnToSpawn,
        String animation,
        boolean clearPhases
) {}
