package com.vitae.demo;

import com.mojang.serialization.MapCodec;
import com.vitae.registry.VitaeContent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class VitaeDemoSpawnerBlock extends BaseEntityBlock {

    public static final MapCodec<VitaeDemoSpawnerBlock> CODEC = simpleCodec(VitaeDemoSpawnerBlock::new);

    public VitaeDemoSpawnerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VitaeDemoSpawnerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, VitaeContent.ANGRY_CUBE_SPAWNER_BLOCK_ENTITY.get(), VitaeDemoSpawnerBlockEntity::serverTick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, net.minecraft.world.entity.player.Player player, net.minecraft.world.phys.BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof VitaeDemoSpawnerBlockEntity spawner)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            int next = spawner.cycleActivationDistance();
            player.displayClientMessage(
                    Component.literal("Angry Cube spawner activation distance: " + next + " blocks").withStyle(ChatFormatting.GREEN),
                    true
            );
        }
        return InteractionResult.SUCCESS;
    }
}
