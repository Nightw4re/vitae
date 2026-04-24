package com.vitae.demo;

import com.vitae.ability.AbilityCastScheduler;
import com.vitae.ability.executor.CarryTargetExecutor;
import com.vitae.ability.BossAbilityRuntime;
import com.vitae.effect.VitaeEffectHooks;
import com.vitae.entity.VitaeBossBar;
import com.vitae.phase.PhaseLockController;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.raid.Raid;

public class VitaeTestEntity extends Vindicator {
    private final VitaeBossBar bossBar;
    private final BossAbilityRuntime abilityRuntime = new BossAbilityRuntime();
    private final AbilityCastScheduler abilityScheduler = new AbilityCastScheduler();
    private final PhaseLockController phaseLockController = new PhaseLockController();
    private boolean dragonDeathStarted;
    private boolean castLocked;

    public VitaeTestEntity(EntityType<? extends Vindicator> type, Level level) {
        super(type, level);
        xpReward = VitaeDemoDefinition.testEntityDefinition().xpRewardOrDefault();
        bossBar = new VitaeBossBar(VitaeDemoDefinition.testEntityDefinition().bossBar(), "Angry Cube");
        applyConfiguredEquipment();
    }

    public static AttributeSupplier.Builder createAttributes() {
        var definition = VitaeDemoDefinition.testEntityDefinition();
        return Vindicator.createAttributes()
                .add(Attributes.MAX_HEALTH, definition.attributes().maxHealth())
                .add(Attributes.FOLLOW_RANGE, definition.attributes().followRange())
                .add(Attributes.MOVEMENT_SPEED, definition.attributes().movementSpeed())
                .add(Attributes.ATTACK_DAMAGE, definition.attributes().attackDamage())
                .add(Attributes.ARMOR, definition.attributes().armor());
    }

    @Override
    protected void populateDefaultEquipmentSlots(net.minecraft.util.RandomSource random, net.minecraft.world.DifficultyInstance difficulty) {
        applyConfiguredEquipment();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, SpawnGroupData spawnGroupData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
        setPersistenceRequired();
        return data;
    }

    @Override
    public void applyRaidBuffs(ServerLevel level, int wave, boolean unused) {
        setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        phaseLockController.tick(this, VitaeDemoDefinition.testEntityDefinition());
        castLocked = CarryTargetExecutor.hasActiveGrab(this) || abilityScheduler.isCasting() || abilityRuntime.isSpinActive() || phaseLockController.isSummonLockActive();

        bossBar.setProgress(getHealth() / getMaxHealth());
        CarryTargetExecutor.tick(this);
        if (CarryTargetExecutor.hasActiveGrab(this)) {
            getNavigation().stop();
            setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
            setAggressive(false);
            return;
        }
        if (!phaseLockController.isSummonLockActive()) {
            abilityScheduler.tick(this, VitaeDemoDefinition.testEntityDefinition());
        }
        if (!abilityScheduler.isCasting() && !phaseLockController.isSummonLockActive()) {
            abilityRuntime.tick(this, VitaeDemoDefinition.testEntityDefinition());
        }

        if (castLocked) {
            getNavigation().stop();
            setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
            setAggressive(false);
        }

        if (tickCount % 20 == 0 && (getTarget() == null || !getTarget().isAlive())) {
            Player nearestPlayer = level().getNearestPlayer(this, VitaeDemoDefinition.testEntityDefinition().attributes().followRange());
            if (nearestPlayer instanceof ServerPlayer serverPlayer && !serverPlayer.isCreative() && !serverPlayer.isSpectator()) {
                setTarget(serverPlayer);
                setAggressive(true);
            }
        }
    }

    @Override
    public void travel(net.minecraft.world.phys.Vec3 travelVector) {
        if (castLocked) {
            setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
            return;
        }
        super.travel(travelVector);
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity entity) {
        if (CarryTargetExecutor.hasActiveGrab(this) || abilityScheduler.isCasting() || abilityRuntime.isSpinActive() || phaseLockController.isSummonLockActive()) {
            return false;
        }
        return super.doHurtTarget(entity);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer serverPlayer) {
        super.startSeenByPlayer(serverPlayer);
        bossBar.addPlayer(serverPlayer);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer serverPlayer) {
        super.stopSeenByPlayer(serverPlayer);
        bossBar.removePlayer(serverPlayer);
    }

    @Override
    public void remove(RemovalReason reason) {
        bossBar.removeAllPlayers();
        super.remove(reason);
    }

    @Override
    protected void tickDeath() {
        deathTime++;
        CarryTargetExecutor.forceReset(this);
        if (!dragonDeathStarted) {
            dragonDeathStarted = true;
            level().globalLevelEvent(1028, blockPosition(), 0);
            level().playSound(null, blockPosition(), SoundEvents.ENDER_DRAGON_DEATH, SoundSource.HOSTILE, 5.0F, 1.0F);
            VitaeEffectHooks.playDeathBurst(level(), this);
        }

        if (!level().isClientSide && level() instanceof ServerLevel serverLevel && deathTime % 10 == 0) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY(0.5D), getZ(), 1, 0.4D, 0.4D, 0.4D, 0.0D);
        }

        if (deathTime >= 200 && !level().isClientSide() && !isRemoved()) {
            remove(RemovalReason.KILLED);
        }
    }

    public int getExperienceReward() {
        return VitaeDemoDefinition.testEntityDefinition().xpRewardOrDefault();
    }

    public double currentSummonLockFloorOrDefault() {
        return VitaeDemoDefinition.testEntityDefinition().nextPhaseHealthFloorOrDefault(getHealth() / getMaxHealth());
    }

    public boolean isSummonLockActive() {
        return phaseLockController.isSummonLockActive();
    }

    private void applyConfiguredEquipment() {
        String mainHandItem = VitaeDemoDefinition.testEntityDefinition().equipmentOrDefault().mainHandItem();
        if (mainHandItem == null || mainHandItem.isBlank()) {
            setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            return;
        }

        var item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(mainHandItem));
        setItemSlot(EquipmentSlot.MAINHAND, item == null ? ItemStack.EMPTY : new ItemStack(item));
    }

}
