package com.vitae.demo;

import com.mojang.brigadier.CommandDispatcher;
import com.vitae.registry.VitaeContent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import static com.vitae.VitaeMod.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public final class VitaeDemoCommand {

    private VitaeDemoCommand() {}

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("vitae")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("spawn_angry_boy")
                                .executes(context -> spawn(context.getSource())))
        );
    }

    private static int spawn(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        BlockPos spawnPos = BlockPos.containing(source.getPosition());
        EntityType<?> entityType = VitaeContent.TEST_ENTITY.get();
        if (entityType.spawn(level, null, null, spawnPos, MobSpawnType.COMMAND, true, false) == null) {
            source.sendFailure(Component.literal("Failed to spawn the Vitae demo entity."));
            return 0;
        }

        source.sendSuccess(() -> Component.literal("Spawned Vitae demo entity at " + spawnPos.toShortString() + "."), true);
        return 1;
    }
}
