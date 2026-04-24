package com.vitae.registry;

import com.vitae.data.AbilityDefinition;
import com.vitae.data.AbilityDefinitionLoader;
import com.vitae.data.EntityDefinition;
import com.vitae.data.EntityDefinitionLoader;
import com.vitae.data.NpcDefinition;
import com.vitae.data.NpcDefinitionLoader;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.core.registries.Registries;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Loads and caches all Vitae entity and NPC definitions from active datapacks.
 *
 * <p>Implements {@link PreparableReloadListener} so it is registered via
 * {@code AddReloadListenerEvent} and reloaded on every {@code /reload}.
 */
public final class VitaeRegistry implements PreparableReloadListener {

    private static final VitaeRegistry INSTANCE = new VitaeRegistry();

    private static final String ENTITY_PREFIX  = "vitae/entities";
    private static final String ABILITY_PREFIX = "vitae/abilities";
    private static final String NPC_PREFIX     = "vitae/npcs";
    private static final String LOOT_PREFIX    = "loot_table";
    private static final String SUFFIX         = ".json";

    private final Map<ResourceLocation, EntityDefinition> entities = new HashMap<>();
    private final Map<ResourceLocation, AbilityDefinition> abilities = new HashMap<>();
    private final Map<ResourceLocation, NpcDefinition> npcs = new HashMap<>();
    private final Map<ResourceLocation, LootTable> loadedLootTables = new HashMap<>();

    private VitaeRegistry() {}

    public static VitaeRegistry get() {
        return INSTANCE;
    }

    @Override
    public CompletableFuture<Void> reload(
            PreparationBarrier barrier,
            ResourceManager manager,
            ProfilerFiller prepareProfiler,
            ProfilerFiller applyProfiler,
            Executor backgroundExecutor,
            Executor gameExecutor
    ) {
        return CompletableFuture
                .supplyAsync(() -> loadAll(manager), backgroundExecutor)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(data -> {
                    entities.clear();
                    entities.putAll(data.entities());
                    abilities.clear();
                    abilities.putAll(data.abilities());
                    npcs.clear();
                    npcs.putAll(data.npcs());
                    loadedLootTables.clear();
                    loadedLootTables.putAll(data.lootTables());
                }, gameExecutor);
    }

    private LoadedData loadAll(ResourceManager manager) {
        Map<ResourceLocation, EntityDefinition> loadedEntities = new HashMap<>();
        Map<ResourceLocation, AbilityDefinition> loadedAbilities = new HashMap<>();
        Map<ResourceLocation, NpcDefinition> loadedNpcs = new HashMap<>();
        Map<ResourceLocation, LootTable> loadedLootTables = new HashMap<>();

        manager.listResources(ENTITY_PREFIX, path -> path.getPath().endsWith(SUFFIX))
                .forEach((location, resource) -> {
                    try (InputStream stream = resource.open()) {
                        String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                        ResourceLocation id = toDefinitionId(location, ENTITY_PREFIX);
                        EntityDefinition definition = EntityDefinitionLoader.parse(json);
                        loadedEntities.put(id, definition);
                        System.out.println("[Vitae] Loaded entity definition " + id);
                    } catch (IOException | IllegalArgumentException e) {
                        System.err.println("[Vitae] Failed to load entity definition " + location + ": " + e.getMessage());
                    }
                });

        manager.listResources(ABILITY_PREFIX, path -> path.getPath().endsWith(SUFFIX))
                .forEach((location, resource) -> {
                    try (InputStream stream = resource.open()) {
                        String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                        ResourceLocation id = toDefinitionId(location, ABILITY_PREFIX);
                        loadedAbilities.put(id, AbilityDefinitionLoader.parse(json));
                        System.out.println("[Vitae] Loaded ability definition " + id);
                    } catch (IOException | IllegalArgumentException e) {
                        System.err.println("[Vitae] Failed to load ability definition " + location + ": " + e.getMessage());
                    }
                });

        manager.listResources(NPC_PREFIX, path -> path.getPath().endsWith(SUFFIX))
                .forEach((location, resource) -> {
                    try (InputStream stream = resource.open()) {
                        String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                        ResourceLocation id = toDefinitionId(location, NPC_PREFIX);
                        loadedNpcs.put(id, NpcDefinitionLoader.parse(json));
                        System.out.println("[Vitae] Loaded NPC definition " + id);
                    } catch (IOException | IllegalArgumentException e) {
                        System.err.println("[Vitae] Failed to load NPC definition " + location + ": " + e.getMessage());
                    }
                });

        var lootResources = manager.listResources(LOOT_PREFIX, path -> path.getNamespace().equals("vitae") && path.getPath().endsWith(SUFFIX));
        lootResources
                .forEach((location, resource) -> {
                    try (InputStream stream = resource.open()) {
                        JsonElement json = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                        ResourceLocation id = toDefinitionId(location, LOOT_PREFIX);
                        LootTable.DIRECT_CODEC.parse(JsonOps.INSTANCE, json).result().ifPresent(table -> loadedLootTables.put(id, table));
                    } catch (IOException | IllegalArgumentException e) {
                        System.err.println("[Vitae] Failed to load loot table " + location + ": " + e.getMessage());
                    }
                });

        return new LoadedData(loadedEntities, loadedAbilities, loadedNpcs, loadedLootTables);
    }

    public EntityDefinition getEntity(ResourceLocation id) { return entities.get(id); }
    public LootTable getLootTable(ResourceLocation id) { return loadedLootTables.get(id); }
    public AbilityDefinition getAbility(ResourceLocation id) { return abilities.get(id); }
    public NpcDefinition getNpc(ResourceLocation id) { return npcs.get(id); }
    public Map<ResourceLocation, EntityDefinition> getEntities() { return Map.copyOf(entities); }
    public Map<ResourceLocation, AbilityDefinition> getAbilities() { return Map.copyOf(abilities); }
    public Map<ResourceLocation, NpcDefinition> getNpcs() { return Map.copyOf(npcs); }

    private static ResourceLocation toDefinitionId(ResourceLocation location, String prefix) {
        String path = location.getPath();
        String trimmed = path.substring(prefix.length() + 1, path.length() - SUFFIX.length());
        return ResourceLocation.fromNamespaceAndPath(location.getNamespace(), trimmed);
    }

    private record LoadedData(
            Map<ResourceLocation, EntityDefinition> entities,
            Map<ResourceLocation, AbilityDefinition> abilities,
            Map<ResourceLocation, NpcDefinition> npcs,
            Map<ResourceLocation, LootTable> lootTables
    ) {}
}
