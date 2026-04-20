package com.vitae.data;

/**
 * Conditions that must be met for an ability to activate.
 *
 * @param minRange        minimum distance to target in blocks (0 = no minimum)
 * @param maxRange        maximum distance to target in blocks (-1 = no maximum)
 * @param minHealthPercent minimum health fraction of the caster (0.0 = always)
 * @param maxHealthPercent maximum health fraction of the caster (1.0 = always)
 * @param chance          probability of activation per eligible tick (0.0–1.0)
 */
public record AbilityCondition(
        double minRange,
        double maxRange,
        double minHealthPercent,
        double maxHealthPercent,
        double chance
) {
    public static AbilityCondition defaults() {
        return new AbilityCondition(0.0, -1.0, 0.0, 1.0, 1.0);
    }

    /** Returns true if the given range satisfies this condition. */
    public boolean matchesRange(double distance) {
        if (distance < minRange) return false;
        if (maxRange >= 0 && distance > maxRange) return false;
        return true;
    }

    /** Returns true if the given health percent satisfies this condition. */
    public boolean matchesHealth(double healthPercent) {
        return healthPercent >= minHealthPercent && healthPercent <= maxHealthPercent;
    }
}
