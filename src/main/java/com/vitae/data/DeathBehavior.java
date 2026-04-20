package com.vitae.data;

/**
 * Controls what happens when a Vitae boss dies.
 *
 * @param animation      cinematic animation to play before despawning (nullable = no animation)
 * @param delayLoot      if true, loot drops only after the death animation finishes
 * @param becomeFriendly if true, the entity becomes a passive NPC instead of dying
 */
public record DeathBehavior(
        String animation,
        boolean delayLoot,
        boolean becomeFriendly
) {}
