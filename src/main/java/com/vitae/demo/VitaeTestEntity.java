package com.vitae.demo;

import com.vitae.ability.AbilityCastScheduler;
import com.vitae.ability.executor.CarryTargetExecutor;
import com.vitae.ability.BossAbilityRuntime;
import com.vitae.effect.VitaeEffectHooks;
import com.vitae.entity.VitaeBossBar;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;

public class VitaeTestEntity extends Vindicator {
    private final VitaeBossBar bossBar;
    private final BossAbilityRuntime abilityRuntime = new BossAbilityRuntime();
    private final AbilityCastScheduler abilityScheduler = new AbilityCastScheduler();
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
    protected void customServerAiStep() {
        castLocked = CarryTargetExecutor.hasActiveGrab(this) || abilityScheduler.isCasting() || abilityRuntime.isSpinActive();

        super.customServerAiStep();

        bossBar.setProgress(getHealth() / getMaxHealth());
        CarryTargetExecutor.tick(this);
        if (CarryTargetExecutor.hasActiveGrab(this)) {
            return;
        }
        abilityScheduler.tick(this, VitaeDemoDefinition.testEntityDefinition());
        if (!abilityScheduler.isCasting()) {
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
        if (CarryTargetExecutor.hasActiveGrab(this) || abilityScheduler.isCasting() || abilityRuntime.isSpinActive()) {
            return false;
        }
        return super.doHurtTarget(entity);
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
            spawnLootChestIfConfigured();
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

    private void spawnLootChestIfConfigured() {
        var definition = VitaeDemoDefinition.testEntityDefinition();
        if (definition.deathBehavior() == null || !definition.deathBehavior().spawnLootChest()) {
            return;
        }
        if (definition.lootTable() == null || definition.lootTable().isBlank() || !(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockPos chestPos = blockPosition().above();
        if (!serverLevel.getBlockState(chestPos).isAir()) {
            chestPos = blockPosition();
        }
        if (!serverLevel.getBlockState(chestPos).isAir()) {
            return;
        }

        serverLevel.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);
        BlockEntity blockEntity = serverLevel.getBlockEntity(chestPos);
        if (!(blockEntity instanceof ChestBlockEntity chest)) {
            return;
        }

        ResourceKey<LootTable> lootKey = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(definition.lootTable()));
        chest.setLootTable(lootKey, serverLevel.getRandom().nextLong());
        chest.unpackLootTable(null);
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
