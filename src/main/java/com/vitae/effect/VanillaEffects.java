package com.vitae.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

public final class VanillaEffects {

    private VanillaEffects() {}

    public static void onSpinStart(Level level, LivingEntity entity) {
        if (level.isClientSide()) {
            return;
        }
        level.playSound(null, entity.blockPosition(), SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.0F, 0.8F);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, entity.getX(), entity.getY(0.5D), entity.getZ(), 18, 0.8D, 0.2D, 0.8D, 0.0D);
            serverLevel.sendParticles(ParticleTypes.CRIT, entity.getX(), entity.getY(0.6D), entity.getZ(), 20, 0.7D, 0.4D, 0.7D, 0.08D);
        }
    }

    public static void onSpinPulse(Level level, LivingEntity entity) {
        if (level.isClientSide()) {
            return;
        }
        level.playSound(null, entity.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 0.9F, 1.1F);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, entity.getX(), entity.getY(0.5D), entity.getZ(), 10, 1.0D, 0.2D, 1.0D, 0.02D);
            serverLevel.sendParticles(ParticleTypes.END_ROD, entity.getX(), entity.getY(0.8D), entity.getZ(), 6, 0.4D, 0.3D, 0.4D, 0.01D);
        }
    }

    public static void onDeathBurst(Level level, LivingEntity entity) {
        if (level.isClientSide()) {
            return;
        }
        level.playSound(null, entity.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 1.6F, 0.7F);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, entity.getX(), entity.getY(0.5D), entity.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            serverLevel.sendParticles(ParticleTypes.DRAGON_BREATH, entity.getX(), entity.getY(0.7D), entity.getZ(), 60, 0.9D, 0.6D, 0.9D, 0.02D);
        }
    }
}
