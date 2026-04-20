package com.vitae.ability;

import java.util.HashMap;
import java.util.Map;

/**
 * Tracks per-ability cooldowns for a single entity.
 *
 * <p>Pure Java — no Minecraft dependency. Tick-driven: call {@link #tick()} every game tick.
 */
public final class AbilityCooldownTracker {

    private final Map<String, Integer> remaining = new HashMap<>();

    /**
     * Decrements all active cooldowns by one tick.
     * Should be called once per game tick per entity.
     */
    public void tick() {
        remaining.replaceAll((id, ticks) -> Math.max(0, ticks - 1));
    }

    /** Returns true if the ability with the given ID is ready to use. */
    public boolean isReady(String abilityId) {
        return remaining.getOrDefault(abilityId, 0) == 0;
    }

    /** Starts the cooldown for the given ability. */
    public void startCooldown(String abilityId, int ticks) {
        remaining.put(abilityId, ticks);
    }

    /** Returns the remaining cooldown ticks for the given ability (0 if ready). */
    public int getRemaining(String abilityId) {
        return remaining.getOrDefault(abilityId, 0);
    }

    /** Resets all cooldowns (e.g. on boss reset). */
    public void resetAll() {
        remaining.clear();
    }
}
