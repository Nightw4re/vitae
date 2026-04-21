package com.vitae.demo;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.VindicatorRenderer;

public class VitaeDemoEntityRenderer extends VindicatorRenderer {

    public VitaeDemoEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
    }
}
