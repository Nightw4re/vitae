package com.vitae.data;

/**
 * Defines the boss bar appearance for a Vitae boss.
 *
 * @param color     boss bar color — matches vanilla BossEvent.BossBarColor names (e.g. "red", "purple")
 * @param overlay   boss bar overlay style — matches BossEvent.BossBarOverlay names (e.g. "progress", "notched_10")
 * @param text      custom display name shown on the boss bar (nullable = use entity display name)
 */
public record BossBarDefinition(
        String color,
        String overlay,
        String text
) {
    public static BossBarDefinition defaults() {
        return new BossBarDefinition("purple", "progress", null);
    }
}
