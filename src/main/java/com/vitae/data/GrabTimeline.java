package com.vitae.data;

/** Pure timing helpers for the grab spell. */
public final class GrabTimeline {
    private GrabTimeline() {}

    public static double progress(int ticks, int durationTicks) {
        if (durationTicks <= 0) {
            return 1.0D;
        }
        return Math.max(0.0D, Math.min(1.0D, (double) ticks / (double) durationTicks));
    }

    public static double targetY(double initialY, double heightOffset, int ticks, int durationTicks) {
        return initialY + (heightOffset * progress(ticks, durationTicks));
    }

    public static boolean shouldRelease(int ticks, int durationTicks) {
        return ticks >= Math.max(1, durationTicks);
    }
}
