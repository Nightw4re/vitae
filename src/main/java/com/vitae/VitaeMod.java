package com.vitae;

import com.vitae.registry.VitaeContent;
import com.vitae.registry.VitaeRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@Mod(VitaeMod.MOD_ID)
public class VitaeMod {

    public static final String MOD_ID = "vitae";

    public VitaeMod(IEventBus modEventBus) {
        VitaeContent.BLOCKS.register(modEventBus);
        VitaeContent.ENTITY_TYPES.register(modEventBus);
        VitaeContent.ITEMS.register(modEventBus);
        VitaeContent.BLOCK_ENTITY_TYPES.register(modEventBus);
        NeoForge.EVENT_BUS.addListener(this::onAddReloadListeners);
    }

    private void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(VitaeRegistry.get());
    }
}
