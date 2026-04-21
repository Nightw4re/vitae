package com.vitae.registry;

import com.vitae.demo.VitaeDemoSpawnEggItem;
import com.vitae.demo.VitaeDemoSpawnerBlock;
import com.vitae.demo.VitaeDemoSpawnerBlockEntity;
import com.vitae.demo.VitaeTestEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.vitae.VitaeMod.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public final class VitaeContent {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredHolder<Block, VitaeDemoSpawnerBlock> ANGRY_CUBE_SPAWNER =
            BLOCKS.register("angry_boy_one_time_spawner",
                    id -> new VitaeDemoSpawnerBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(5.0F)
                            .requiresCorrectToolForDrops()));

    public static final DeferredHolder<EntityType<?>, EntityType<VitaeTestEntity>> TEST_ENTITY =
            ENTITY_TYPES.register("angry_boy", id ->
                    EntityType.Builder.of(VitaeTestEntity::new, MobCategory.MONSTER)
                            .sized(1.8F, 1.8F)
                            .build(id.toString())
            );

    public static final DeferredHolder<Item, Item> TEST_ENTITY_SPAWN_EGG =
            ITEMS.register("angry_boy_spawn_egg",
                    id -> new VitaeDemoSpawnEggItem(TEST_ENTITY, 0x5F8DD3, 0xD94F70, new Item.Properties()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VitaeDemoSpawnerBlockEntity>> ANGRY_CUBE_SPAWNER_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("angry_boy_one_time_spawner",
                    () -> BlockEntityType.Builder.of(VitaeDemoSpawnerBlockEntity::new, ANGRY_CUBE_SPAWNER.get()).build(null));

    public static final DeferredHolder<Item, Item> TEST_ENTITY_ONE_TIME_SPAWNER_ITEM =
            ITEMS.register("angry_boy_one_time_spawner",
                    id -> new BlockItem(ANGRY_CUBE_SPAWNER.get(), new Item.Properties()));

    private VitaeContent() {}

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(TEST_ENTITY.get(), VitaeTestEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void onBuildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(TEST_ENTITY_SPAWN_EGG.get());
        }
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(TEST_ENTITY_ONE_TIME_SPAWNER_ITEM.get());
        }
    }
}
