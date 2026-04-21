package com.vitae;

import com.vitae.registry.VitaeContent;
import com.vitae.registry.VitaeRegistry;
import com.vitae.data.EntityDefinition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.registries.Registries;

@Mod(VitaeMod.MOD_ID)
public class VitaeMod {

    public static final String MOD_ID = "vitae";

    public VitaeMod(IEventBus modEventBus) {
        VitaeContent.BLOCKS.register(modEventBus);
        VitaeContent.ENTITY_TYPES.register(modEventBus);
        VitaeContent.ITEMS.register(modEventBus);
        VitaeContent.BLOCK_ENTITY_TYPES.register(modEventBus);
        NeoForge.EVENT_BUS.addListener(this::onAddReloadListeners);
        NeoForge.EVENT_BUS.addListener(this::onEntityJoinLevel);
    }

    private void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(VitaeRegistry.get());
    }

    private void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Mob mob) || !(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (mob.getSpawnType() == null || !isNaturalSpawn(mob.getSpawnType())) {
            return;
        }

        ResourceLocation entityId = net.minecraft.world.entity.EntityType.getKey(mob.getType());
        var definition = VitaeRegistry.get().getEntity(entityId);
        if (definition == null || !definition.hasNaturalSpawnRestrictions()) {
            return;
        }
        if (definition.isBoss()) {
            return;
        }

        String biomeId = level.getBiome(mob.blockPosition()).unwrapKey().map(key -> key.location().toString()).orElse(null);
        if (!definition.canSpawnInBiome(biomeId)) {
            event.setCanceled(true);
            return;
        }

        if (definition.hasSpawnStructure() || !definition.spawnRulesOrDefault().structuresOrDefault().isEmpty()) {
            if (!canSpawnInStructure(level, mob.blockPosition(), definition)) {
                event.setCanceled(true);
                return;
            }
        }

        if (!withinSpawnCaps(level, mob.blockPosition(), mob.getType(), definition)) {
            event.setCanceled(true);
        }
    }

    private boolean canSpawnInStructure(ServerLevel level, BlockPos pos, com.vitae.data.EntityDefinition definition) {
        var registry = level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.STRUCTURE);
        for (String structureId : definition.spawnRulesOrDefault().structuresOrDefault()) {
            ResourceLocation id = parseResourceLocation(structureId);
            if (id == null || registry.get(id) == null) {
                continue;
            }
            StructureStart start = level.structureManager().getStructureWithPieceAt(pos, registry.get(id));
            if (start != null && start.isValid()) {
                return true;
            }
        }
        if (definition.hasSpawnStructure()) {
            ResourceLocation id = parseResourceLocation(definition.spawnStructureOrNull());
            if (id != null && registry.get(id) != null) {
                StructureStart start = level.structureManager().getStructureWithPieceAt(pos, registry.get(id));
                return start != null && start.isValid();
            }
        }
        return false;
    }

    private boolean withinSpawnCaps(ServerLevel level, BlockPos pos, net.minecraft.world.entity.EntityType<?> type, EntityDefinition definition) {
        var rules = definition.spawnRulesOrDefault();
        int nearbyCount = countNearby(level, pos, type, 128.0D);
        if (rules.maxNearbyGlobal() > 0 && nearbyCount >= rules.maxNearbyGlobal()) {
            return false;
        }

        if (rules.maxNearbyPerBiome() > 0) {
            String biomeId = level.getBiome(pos).unwrapKey().map(key -> key.location().toString()).orElse(null);
            if (biomeId != null && countEntitiesInBiome(level, biomeId, type, 128.0D) >= rules.maxNearbyPerBiome()) {
                return false;
            }
        }

        if (rules.maxNearbyPerStructure() > 0 && !rules.structuresOrDefault().isEmpty()) {
            for (String structureId : rules.structuresOrDefault()) {
                ResourceLocation id = parseResourceLocation(structureId);
                if (id == null || level.registryAccess().registryOrThrow(Registries.STRUCTURE).get(id) == null) {
                    continue;
                }
                if (countEntitiesInStructure(level, pos, type, id.toString(), 128.0D) >= rules.maxNearbyPerStructure()) {
                    return false;
                }
            }
        }

        return true;
    }

    private int countNearby(ServerLevel level, BlockPos pos, net.minecraft.world.entity.EntityType<?> type, double radius) {
        AABB box = AABB.ofSize(pos.getCenter(), radius, radius, radius);
        return level.getEntitiesOfClass(Entity.class, box, entity -> entity.getType() == type).size();
    }

    private int countEntitiesInBiome(ServerLevel level, String biomeId, net.minecraft.world.entity.EntityType<?> type, double radius) {
        AABB box = AABB.ofSize(level.getSharedSpawnPos().getCenter(), radius, radius, radius);
        return level.getEntitiesOfClass(Entity.class, box, entity -> entity.getType() == type
                && level.getBiome(entity.blockPosition()).unwrapKey().map(key -> key.location().toString()).map(biomeId::equals).orElse(false)).size();
    }

    private int countEntitiesInStructure(ServerLevel level, BlockPos pos, net.minecraft.world.entity.EntityType<?> type, String structureId, double radius) {
        AABB box = AABB.ofSize(pos.getCenter(), radius, radius, radius);
        return level.getEntitiesOfClass(Entity.class, box, entity -> entity.getType() == type
                && hasStructure(level, entity.blockPosition(), structureId)).size();
    }

    private boolean hasStructure(ServerLevel level, BlockPos pos, String structureId) {
        var registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        ResourceLocation id = parseResourceLocation(structureId);
        if (id == null) {
            return false;
        }
        var structure = registry.get(id);
        if (structure == null) {
            return false;
        }
        StructureStart start = level.structureManager().getStructureWithPieceAt(pos, structure);
        return start != null && start.isValid();
    }

    private boolean isNaturalSpawn(MobSpawnType spawnType) {
        return spawnType == MobSpawnType.NATURAL
                || spawnType == MobSpawnType.CHUNK_GENERATION
                || spawnType == MobSpawnType.STRUCTURE;
    }

    private ResourceLocation parseResourceLocation(String value) {
        try {
            return ResourceLocation.parse(value);
        } catch (Exception e) {
            return null;
        }
    }
}
