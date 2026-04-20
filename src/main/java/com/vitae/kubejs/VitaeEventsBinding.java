package com.vitae.kubejs;

import com.vitae.ability.AbilityExecutor;
import com.vitae.ability.AbilityExecutorRegistry;
import com.vitae.api.VitaeEntityEvent;
import com.vitae.api.VitaeEventBus;

import java.util.function.Consumer;

/**
 * JS-facing binding that bridges {@link VitaeEventBus} to KubeJS event handlers.
 *
 * <p>Registered as {@code VitaeEvents} in server scripts.
 * Connects the internal event bus to KubeJS so fired events reach JS listeners.
 *
 * <p>Also exposes {@link #registerAbility} so pack devs can define custom ability
 * executors entirely in JavaScript:
 * <pre>{@code
 * VitaeEvents.registerAbility("staff_beam", (caster, target, ability) => {
 *     // custom logic here
 * })
 * }</pre>
 */
public final class VitaeEventsBinding {

    public VitaeEventsBinding() {
        VitaeEventBus.get().register(this::dispatch);
    }

    /**
     * Registers a custom ability executor from KubeJS.
     *
     * @param type     ability type string matching the {@code type} field in the ability JSON
     * @param executor JS function {@code (caster, target, ability) => void}
     */
    public void registerAbility(String type, AbilityExecutor executor) {
        AbilityExecutorRegistry.get().register(type, executor);
    }

    private void dispatch(VitaeEntityEvent event) {
        switch (event) {
            case VitaeEntityEvent.Spawn e ->
                VitaeEvents.ON_SPAWN.post(new VitaeSpawnEventJS(e.entityId()));
            case VitaeEntityEvent.Death e ->
                VitaeEvents.ON_DEATH.post(new VitaeDeathEventJS(e.entityId(), e.becameFriendly()));
            case VitaeEntityEvent.PhaseChange e ->
                VitaeEvents.ON_PHASE_CHANGE.post(new VitaePhaseChangeEventJS(e.entityId(), e.previous(), e.next()));
            case VitaeEntityEvent.AbilityUsed e ->
                VitaeEvents.ON_ABILITY_USED.post(new VitaeAbilityEventJS(e.entityId(), e.ability()));
            case VitaeEntityEvent.Reset e ->
                VitaeEvents.ON_RESET.post(new VitaeResetEventJS(e.entityId()));
        }
    }
}
