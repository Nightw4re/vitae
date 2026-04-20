package com.vitae.kubejs;

import com.vitae.api.VitaeEventBus;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;

/**
 * Registers Vitae's KubeJS integration.
 *
 * <p>Exposes {@code VitaeEvents} binding to server scripts so pack devs can listen to
 * entity events and define custom ability executors in JavaScript.
 *
 * <p>Registration is declared in {@code META-INF/services/dev.latvian.mods.kubejs.plugin.KubeJSPlugin}.
 */
public final class VitaeKubePlugin implements KubeJSPlugin {

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(VitaeEvents.GROUP);
    }

    @Override
    public void registerBindings(BindingRegistry registry) {
        registry.add("VitaeEvents", new VitaeEventsBinding());
    }
}
