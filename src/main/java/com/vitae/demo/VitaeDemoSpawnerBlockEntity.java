package com.vitae.demo;

import com.vitae.registry.VitaeContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class VitaeDemoSpawnerBlockEntity extends BlockEntity {

    private static final int[] DISTANCES = {8, 16, 24, 32};

    private int activationDistance = DISTANCES[1];

    public VitaeDemoSpawnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(VitaeContent.ANGRY_CUBE_SPAWNER_BLOCK_ENTITY.get(), pos, blockState);
    }

    public int cycleActivationDistance() {
        int currentIndex = 0;
        for (int i = 0; i < DISTANCES.length; i++) {
            if (DISTANCES[i] == activationDistance) {
                currentIndex = i;
                break;
            }
        }
        activationDistance = DISTANCES[(currentIndex + 1) % DISTANCES.length];
        setChanged();
        return activationDistance;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, VitaeDemoSpawnerBlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        AABB range = new AABB(pos).inflate(blockEntity.activationDistance);
        boolean playerNearby = !serverLevel.getEntitiesOfClass(net.minecraft.server.level.ServerPlayer.class, range).isEmpty();
        if (!playerNearby) {
            return;
        }

        VitaeContent.TEST_ENTITY.get().spawn(serverLevel, null, null, pos.above(), MobSpawnType.TRIGGERED, true, false);
        serverLevel.destroyBlock(pos, false);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("activation_distance")) {
            activationDistance = tag.getInt("activation_distance");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("activation_distance", activationDistance);
    }
}
