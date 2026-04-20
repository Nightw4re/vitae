package com.vitae;

import com.vitae.registry.VitaeRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@Mod(VitaeMod.MOD_ID)
public class VitaeMod {

    public static final String MOD_ID = "vitae";

    public VitaeMod(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.addListener(this::onAddReloadListeners);
    }

    private void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(VitaeRegistry.get());
    }
}
