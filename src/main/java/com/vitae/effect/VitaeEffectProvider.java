package com.vitae.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface VitaeEffectProvider {
    default void onSpinStart(Level level, LivingEntity entity) {}

    default void onSpinPulse(Level level, LivingEntity entity) {}

    default void onDeathBurst(Level level, LivingEntity entity) {}
}
