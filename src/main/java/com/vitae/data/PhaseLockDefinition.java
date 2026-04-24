package com.vitae.data;

/**
 * Optional summon-lock behavior that spawns support entities and may keep the boss
 * invulnerable while those summons are alive.
 *
 * @param summonEntity       entity ID to spawn as support units
 * @param summonCount        number of support units to spawn
 * @param invulnerableWhileSummonsAlive whether the boss becomes invulnerable while summons live
 */
public record PhaseLockDefinition(
        String summonEntity,
        int summonCount,
        boolean invulnerableWhileSummonsAlive
) {}
