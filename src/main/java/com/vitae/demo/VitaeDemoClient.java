package com.vitae.demo;

import com.vitae.registry.VitaeContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import static com.vitae.VitaeMod.MOD_ID;

@EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class VitaeDemoClient {

    private VitaeDemoClient() {}

    @SubscribeEvent
    public static void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(VitaeContent.TEST_ENTITY.get(), VitaeDemoEntityRenderer::new);
    }
}
