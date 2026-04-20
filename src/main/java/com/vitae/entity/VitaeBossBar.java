package com.vitae.entity;

import com.vitae.data.BossBarDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;

/**
 * Manages the server-side boss bar for a Vitae boss entity.
 *
 * <p>Create one instance per boss entity. Call {@link #addPlayer}/{@link #removePlayer}
 * as players enter/leave tracking range, and {@link #setProgress(float)} each tick.
 */
public final class VitaeBossBar {

    private final ServerBossEvent bossEvent;

    public VitaeBossBar(BossBarDefinition definition, String entityDisplayName) {
        String text = definition.text() != null ? definition.text() : entityDisplayName;
        this.bossEvent = new ServerBossEvent(
                Component.literal(text),
                parseColor(definition.color()),
                parseOverlay(definition.overlay())
        );
    }

    public void addPlayer(ServerPlayer player) {
        bossEvent.addPlayer(player);
    }

    public void removePlayer(ServerPlayer player) {
        bossEvent.removePlayer(player);
    }

    public void removeAllPlayers() {
        bossEvent.removeAllPlayers();
    }

    /** Updates the boss bar fill (0.0 = empty, 1.0 = full). */
    public void setProgress(float healthPercent) {
        bossEvent.setProgress(Math.clamp(healthPercent, 0.0f, 1.0f));
    }

    /** Updates the display name — called on phase change if the phase has custom text. */
    public void setText(String text) {
        bossEvent.setName(Component.literal(text));
    }

    private static BossEvent.BossBarColor parseColor(String color) {
        if (color == null) return BossEvent.BossBarColor.PURPLE;
        return switch (color.toLowerCase()) {
            case "pink"   -> BossEvent.BossBarColor.PINK;
            case "blue"   -> BossEvent.BossBarColor.BLUE;
            case "red"    -> BossEvent.BossBarColor.RED;
            case "green"  -> BossEvent.BossBarColor.GREEN;
            case "yellow" -> BossEvent.BossBarColor.YELLOW;
            case "white"  -> BossEvent.BossBarColor.WHITE;
            default       -> BossEvent.BossBarColor.PURPLE;
        };
    }

    private static BossEvent.BossBarOverlay parseOverlay(String overlay) {
        if (overlay == null) return BossEvent.BossBarOverlay.PROGRESS;
        return switch (overlay.toLowerCase()) {
            case "notched_6"  -> BossEvent.BossBarOverlay.NOTCHED_6;
            case "notched_10" -> BossEvent.BossBarOverlay.NOTCHED_10;
            case "notched_12" -> BossEvent.BossBarOverlay.NOTCHED_12;
            case "notched_20" -> BossEvent.BossBarOverlay.NOTCHED_20;
            default           -> BossEvent.BossBarOverlay.PROGRESS;
        };
    }
}
