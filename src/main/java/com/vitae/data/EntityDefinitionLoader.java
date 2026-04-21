package com.vitae.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses a Vitae entity JSON definition into an {@link EntityDefinition}.
 *
 * <p>Uses Gson from the Minecraft classpath — this class is Minecraft-dependent and
 * cannot be used in plain-Java unit tests.
 */
public final class EntityDefinitionLoader {

    private EntityDefinitionLoader() {}

    public static EntityDefinition parse(String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();

        String model = requireString(root, "model");
        String animations = requireString(root, "animations");
        int xpReward = (int) getDouble(root, "xp_reward", -1.0);
        String lootTable = getString(root, "loot_table", null);
        String introAnimation = getString(root, "intro_animation", null);
        String spawnStructure = getString(root, "spawn_structure", null);

        AttributeDefinition attributes = parseAttributes(root);
        List<PhaseDefinition> phases = parsePhases(root);
        List<AbilityReference> abilities = parseAbilityReferences(root);
        DeathBehavior deathBehavior = parseDeathBehavior(root);
        ResetBehavior resetBehavior = parseResetBehavior(root);
        BossBarDefinition bossBar = parseBossBar(root);
        CombatDefinition combat = parseCombat(root);
        EquipmentDefinition equipment = parseEquipment(root);
        SpawnRules spawnRules = parseSpawnRules(root);

        return new EntityDefinition(model, animations, attributes, phases, abilities, xpReward, lootTable,
                introAnimation, deathBehavior, resetBehavior, bossBar, combat, equipment, spawnStructure, spawnRules);
    }

    private static AttributeDefinition parseAttributes(JsonObject root) {
        AttributeDefinition defaults = AttributeDefinition.defaults();
        if (!root.has("attributes")) return defaults;
        JsonObject a = root.getAsJsonObject("attributes");
        return new AttributeDefinition(
                getDouble(a, "max_health", defaults.maxHealth()),
                getDouble(a, "follow_range", defaults.followRange()),
                getDouble(a, "movement_speed", defaults.movementSpeed()),
                getDouble(a, "attack_damage", defaults.attackDamage()),
                getDouble(a, "armor", defaults.armor())
        );
    }

    private static List<PhaseDefinition> parsePhases(JsonObject root) {
        if (!root.has("phases")) return List.of();
        JsonArray arr = root.getAsJsonArray("phases");
        List<PhaseDefinition> phases = new ArrayList<>(arr.size());
        for (JsonElement el : arr) {
            JsonObject p = el.getAsJsonObject();
            String id = requireString(p, "id");
            double threshold = getDouble(p, "health_threshold", 1.0);
            String animation = getString(p, "animation", null);
            String model = getString(p, "model", null);
            double scale = getDouble(p, "scale", PhaseDefinition.DEFAULT_SCALE);
            PhaseTransitionDefinition transition = parseTransition(p);
            phases.add(new PhaseDefinition(id, threshold, List.of(), animation, model, scale, transition));
        }
        return List.copyOf(phases);
    }

    private static List<AbilityReference> parseAbilityReferences(JsonObject root) {
        if (!root.has("abilities")) return List.of();
        JsonArray arr = root.getAsJsonArray("abilities");
        List<AbilityReference> references = new ArrayList<>(arr.size());
        for (JsonElement el : arr) {
            if (el.isJsonPrimitive()) {
                references.add(new AbilityReference(el.getAsString(), null));
                continue;
            }
            JsonObject ref = el.getAsJsonObject();
            String id = requireString(ref, "id");
            String cooldownTicks = getModifierString(ref, "cooldown_ticks", null);
            references.add(new AbilityReference(id, cooldownTicks));
        }
        return List.copyOf(references);
    }

    private static PhaseTransitionDefinition parseTransition(JsonObject phase) {
        if (!phase.has("transition")) return null;
        JsonObject t = phase.getAsJsonObject("transition");
        String animation = getString(t, "animation", null);
        boolean invulnerable = getBoolean(t, "invulnerable", false);
        int durationTicks = (int) getDouble(t, "duration_ticks", PhaseTransitionDefinition.USE_ANIMATION_LENGTH);
        return new PhaseTransitionDefinition(animation, invulnerable, durationTicks);
    }

    private static DeathBehavior parseDeathBehavior(JsonObject root) {
        if (!root.has("on_death")) return null;
        JsonObject d = root.getAsJsonObject("on_death");
        return new DeathBehavior(
                getString(d, "animation", null),
                getBoolean(d, "delay_loot", false),
                getBoolean(d, "become_friendly", false),
                getBoolean(d, "spawn_loot_chest", false)
        );
    }

    private static ResetBehavior parseResetBehavior(JsonObject root) {
        if (!root.has("on_reset")) return null;
        JsonObject r = root.getAsJsonObject("on_reset");
        return new ResetBehavior(
                getBoolean(r, "full_heal", false),
                getBoolean(r, "return_to_spawn", false),
                getString(r, "animation", null),
                getBoolean(r, "clear_phases", false)
        );
    }

    private static BossBarDefinition parseBossBar(JsonObject root) {
        if (!root.has("boss_bar")) return null;
        JsonObject b = root.getAsJsonObject("boss_bar");
        BossBarDefinition defaults = BossBarDefinition.defaults();
        return new BossBarDefinition(
                getString(b, "color", defaults.color()),
                getString(b, "overlay", defaults.overlay()),
                getString(b, "text", null)
        );
    }

    private static CombatDefinition parseCombat(JsonObject root) {
        CombatDefinition defaults = CombatDefinition.defaults();
        if (!root.has("combat")) return defaults;
        JsonObject c = root.getAsJsonObject("combat");
        return new CombatDefinition(
                getBoolean(c, "basic_melee_enabled", defaults.basicMeleeEnabled()),
                (int) getDouble(c, "basic_melee_cooldown_ticks", defaults.basicMeleeCooldownTicks()),
                getDouble(c, "basic_melee_range", defaults.basicMeleeRange()),
                getBoolean(c, "scale_damage_with_held_weapon", defaults.scaleDamageWithHeldWeapon()),
                getBoolean(c, "scale_attack_speed_with_held_weapon", defaults.scaleAttackSpeedWithHeldWeapon()),
                getDouble(c, "spin_radius", defaults.spinRadius()),
                getBoolean(c, "spin_invulnerable", defaults.spinInvulnerable())
        );
    }

    private static EquipmentDefinition parseEquipment(JsonObject root) {
        EquipmentDefinition defaults = EquipmentDefinition.defaults();
        if (!root.has("equipment")) return defaults;
        JsonObject e = root.getAsJsonObject("equipment");
        return new EquipmentDefinition(
                getNullableString(e, "main_hand", defaults.mainHandItem())
        );
    }

    private static SpawnRules parseSpawnRules(JsonObject root) {
        List<String> biomes = List.of();
        List<String> structures = List.of();
        int maxNearbyGlobal = 0;
        int maxNearbyPerBiome = 0;
        int maxNearbyPerStructure = 0;

        if (root.has("spawn_rules")) {
            JsonObject rules = root.getAsJsonObject("spawn_rules");
            biomes = parseStringArray(rules, "biomes");
            structures = parseStringArray(rules, "structures");
            maxNearbyGlobal = (int) getDouble(rules, "max_nearby_global", 0.0);
            maxNearbyPerBiome = (int) getDouble(rules, "max_nearby_per_biome", 0.0);
            maxNearbyPerStructure = (int) getDouble(rules, "max_nearby_per_structure", 0.0);
        }

        if (root.has("spawn_biomes")) {
            biomes = parseStringArray(root, "spawn_biomes");
        }
        if (root.has("spawn_structures")) {
            structures = parseStringArray(root, "spawn_structures");
        }

        if (biomes.isEmpty() && structures.isEmpty()) {
            if (maxNearbyGlobal <= 0 && maxNearbyPerBiome <= 0 && maxNearbyPerStructure <= 0) {
                return SpawnRules.defaults();
            }
        }
        return new SpawnRules(List.copyOf(biomes), List.copyOf(structures), maxNearbyGlobal, maxNearbyPerBiome, maxNearbyPerStructure);
    }

    private static List<String> parseStringArray(JsonObject obj, String key) {
        List<String> result = new ArrayList<>();
        if (obj.has(key)) {
            for (JsonElement el : obj.getAsJsonArray(key)) {
                result.add(el.getAsString());
            }
        }
        return result;
    }

    private static String requireString(JsonObject obj, String key) {
        if (!obj.has(key)) throw new IllegalArgumentException("Missing required field: " + key);
        return obj.get(key).getAsString();
    }

    private static String getString(JsonObject obj, String key, String fallback) {
        return obj.has(key) ? obj.get(key).getAsString() : fallback;
    }

    private static String getNullableString(JsonObject obj, String key, String fallback) {
        if (!obj.has(key) || obj.get(key).isJsonNull()) {
            return fallback;
        }
        return obj.get(key).getAsString();
    }

    private static String getModifierString(JsonObject obj, String key, String fallback) {
        if (!obj.has(key) || obj.get(key).isJsonNull()) {
            return fallback;
        }
        JsonElement element = obj.get(key);
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return Integer.toString(element.getAsInt());
        }
        return element.getAsString();
    }

    private static double getDouble(JsonObject obj, String key, double fallback) {
        return obj.has(key) ? obj.get(key).getAsDouble() : fallback;
    }

    private static boolean getBoolean(JsonObject obj, String key, boolean fallback) {
        return obj.has(key) ? obj.get(key).getAsBoolean() : fallback;
    }
}
