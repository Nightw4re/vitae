package com.vitae.data;

/**
 * Defines the movement and guard behavior of a Vitae NPC.
 *
 * @param type        behavior type: {@code idle}, {@code follow}, {@code guard}
 * @param followRange maximum distance in blocks before the NPC stops following a player
 * @param guardRadius radius in blocks the NPC patrols around its spawn point
 */
public record NpcBehavior(
        String type,
        double followRange,
        double guardRadius
) {
    public static final String IDLE   = "idle";
    public static final String FOLLOW = "follow";
    public static final String GUARD  = "guard";

    public static NpcBehavior defaults() {
        return new NpcBehavior(IDLE, 16.0, 8.0);
    }

    public boolean isFollow() { return FOLLOW.equals(type); }
    public boolean isGuard()  { return GUARD.equals(type); }
}
