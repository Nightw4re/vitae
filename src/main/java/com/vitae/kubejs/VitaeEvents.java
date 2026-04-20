package com.vitae.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

/**
 * KubeJS event handlers for Vitae entity events.
 *
 * <p>Usage in KubeJS scripts:
 * <pre>{@code
 * VitaeEvents.onSpawn(event => {
 *     console.log('Spawned: ' + event.entityId)
 * })
 *
 * VitaeEvents.onPhaseChange(event => {
 *     if (event.next.id === 'phase_2') {
 *         // custom logic
 *     }
 * })
 * }</pre>
 */
public final class VitaeEvents {

    public static final EventGroup GROUP = EventGroup.of("VitaeEvents");

    public static final EventHandler ON_SPAWN       = GROUP.server("onSpawn",       () -> VitaeSpawnEventJS.class);
    public static final EventHandler ON_DEATH       = GROUP.server("onDeath",       () -> VitaeDeathEventJS.class);
    public static final EventHandler ON_PHASE_CHANGE = GROUP.server("onPhaseChange", () -> VitaePhaseChangeEventJS.class);
    public static final EventHandler ON_ABILITY_USED = GROUP.server("onAbilityUsed", () -> VitaeAbilityEventJS.class);
    public static final EventHandler ON_RESET       = GROUP.server("onReset",       () -> VitaeResetEventJS.class);

    private VitaeEvents() {}
}
