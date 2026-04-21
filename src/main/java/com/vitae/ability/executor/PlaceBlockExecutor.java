package com.vitae.ability.executor;

import com.vitae.ability.AbilityExecutor;
import com.vitae.data.AbilityDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class PlaceBlockExecutor implements AbilityExecutor {
    @Override
    public void execute(Mob caster, LivingEntity target, AbilityDefinition ability) {
        if (caster.level().isClientSide() || !(caster.level() instanceof ServerLevel level)) {
            return;
        }
        String blockId = ability.parameters() != null ? ability.parameters().blockId() : null;
        if (blockId == null || blockId.isBlank()) {
            return;
        }
        BlockPos pos = target != null ? target.blockPosition().below() : caster.blockPosition().below();
        Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(blockId));
        if (block == null) {
            return;
        }
        BlockState state = block.defaultBlockState();
        level.setBlockAndUpdate(pos, state);
    }
}
