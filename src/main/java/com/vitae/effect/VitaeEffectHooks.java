package com.vitae.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.ServiceLoader;

public final class VitaeEffectHooks {

    private static final List<VitaeEffectProvider> PROVIDERS = ServiceLoader
            .load(VitaeEffectProvider.class)
            .stream()
            .map(ServiceLoader.Provider::get)
            .toList();

    private VitaeEffectHooks() {}

    public static void playSpinStart(Level level, LivingEntity entity) {
        for (VitaeEffectProvider provider : PROVIDERS) {
            provider.onSpinStart(level, entity);
        }
        VanillaEffects.onSpinStart(level, entity);
    }

    public static void playSpinPulse(Level level, LivingEntity entity) {
        for (VitaeEffectProvider provider : PROVIDERS) {
            provider.onSpinPulse(level, entity);
        }
        VanillaEffects.onSpinPulse(level, entity);
    }

    public static void playDeathBurst(Level level, LivingEntity entity) {
        for (VitaeEffectProvider provider : PROVIDERS) {
            provider.onDeathBurst(level, entity);
        }
        VanillaEffects.onDeathBurst(level, entity);
    }
}
