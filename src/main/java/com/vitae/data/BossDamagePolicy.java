package com.vitae.data;

/**
 * Pure helper for boss damage floors and summon-lock blocking.
 */
public final class BossDamagePolicy {

    private BossDamagePolicy() {}

    public static float clampDamageToFloor(float currentHealth, float maxHealth, double floorPercent, float incomingDamage) {
        if (maxHealth <= 0.0F || floorPercent <= 0.0D) {
            return incomingDamage;
        }
        float floorHealth = Math.max(1.0F, maxHealth * (float) floorPercent);
        if (currentHealth <= floorHealth) {
            return 0.0F;
        }
        float maxAllowedDamage = currentHealth - floorHealth;
        return Math.min(incomingDamage, maxAllowedDamage);
    }

    public static boolean shouldBlockDamageWhileSummonLocked(boolean summonLockActive) {
        return summonLockActive;
    }
}
