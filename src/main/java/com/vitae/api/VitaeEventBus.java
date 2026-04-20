package com.vitae.api;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Simple event bus for Vitae entity events.
 *
 * <p>Pure Java — no Minecraft or KubeJS dependency. KubeJS plugin registers
 * listeners here; Vitae entity logic fires events here.
 */
public final class VitaeEventBus {

    private static final VitaeEventBus INSTANCE = new VitaeEventBus();

    private final List<Consumer<VitaeEntityEvent>> listeners = new ArrayList<>();

    /** Use {@link #get()} for the global singleton, or this constructor to create an isolated instance for tests. */
    public VitaeEventBus() {}

    public static VitaeEventBus get() {
        return INSTANCE;
    }

    /** Registers a listener that receives all Vitae entity events. */
    public void register(Consumer<VitaeEntityEvent> listener) {
        listeners.add(listener);
    }

    /** Removes a previously registered listener. */
    public void unregister(Consumer<VitaeEntityEvent> listener) {
        listeners.remove(listener);
    }

    /** Fires the given event to all registered listeners. */
    public void fire(VitaeEntityEvent event) {
        for (Consumer<VitaeEntityEvent> listener : listeners) {
            listener.accept(event);
        }
    }

    /** Removes all listeners — used during world unload. */
    public void clear() {
        listeners.clear();
    }
}
