package com.vitae.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses a standalone Vitae ability JSON definition.
 */
public final class AbilityDefinitionLoader {

    private AbilityDefinitionLoader() {}

    public static AbilityDefinition parse(String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        String id = requireString(root, "id");
        String type = requireString(root, "type");
        int cooldownTicks = (int) getDouble(root, "cooldown_ticks", 0.0);
        AbilityCondition condition = parseAbilityCondition(root);
        AbilityParameters parameters = parseAbilityParameters(root);
        List<AbilityStepDefinition> steps = parseAbilitySteps(root);
        int randomDelayMin = (int) getDouble(root, "random_delay_ticks_min", 0.0);
        int randomDelayMax = (int) getDouble(root, "random_delay_ticks_max", 0.0);
        boolean interruptible = getBoolean(root, "interruptible", true);
        return new AbilityDefinition(id, type, cooldownTicks, condition, parameters, List.copyOf(steps), randomDelayMin, randomDelayMax, interruptible);
    }

    private static AbilityCondition parseAbilityCondition(JsonObject obj) {
        if (!obj.has("condition")) return null;
        JsonObject c = obj.getAsJsonObject("condition");
        return new AbilityCondition(
                getDouble(c, "min_range", 0.0),
                getDouble(c, "max_range", -1.0),
                getDouble(c, "min_health_percent", 0.0),
                getDouble(c, "max_health_percent", 1.0),
                getDouble(c, "chance", 1.0)
        );
    }

    private static AbilityParameters parseAbilityParameters(JsonObject obj) {
        if (!obj.has("parameters")) return AbilityParameters.empty();
        JsonObject p = obj.getAsJsonObject("parameters");
        return new AbilityParameters(
                getDouble(p, "damage", 0.0),
                getDouble(p, "knockback", 0.0),
                getString(p, "projectile_id", null),
                getDouble(p, "speed", 1.0),
                getString(p, "summon_id", null),
                (int) getDouble(p, "count", 1.0),
                getDouble(p, "radius", 0.0),
                getString(p, "effect", null),
                getString(p, "provider_id", null),
                getString(p, "spell_id", null),
                getString(p, "entity_id", null),
                getString(p, "block_id", null),
                (int) getDouble(p, "duration_ticks", 0.0),
                getBoolean(p, "invulnerable", false),
                getBoolean(p, "no_ai", false),
                getBoolean(p, "interruptible", true),
                getDouble(p, "height_offset", 0.6D),
                parseSpawnPoints(p),
                getDouble(p, "scale_multiplier", 1.0D)
        );
    }

    private static List<SpawnPointDefinition> parseSpawnPoints(JsonObject obj) {
        if (!obj.has("spawn_points")) {
            return List.of();
        }
        JsonArray arr = obj.getAsJsonArray("spawn_points");
        List<SpawnPointDefinition> points = new ArrayList<>(arr.size());
        for (JsonElement el : arr) {
            JsonObject point = el.getAsJsonObject();
            double x = getDouble(point, "x", 0.0D);
            double z = getDouble(point, "z", 0.0D);
            double y = getDouble(point, "y", 0.0D);
            points.add(new SpawnPointDefinition(x, z, y));
        }
        return List.copyOf(points);
    }

    private static List<AbilityStepDefinition> parseAbilitySteps(JsonObject obj) {
        if (!obj.has("steps")) return List.of();
        JsonArray arr = obj.getAsJsonArray("steps");
        List<AbilityStepDefinition> steps = new ArrayList<>(arr.size());
        for (JsonElement el : arr) {
            JsonObject s = el.getAsJsonObject();
            String ability = requireString(s, "ability");
            int delayTicks = (int) getDouble(s, "delay_ticks", 0.0);
            AbilityParameters parameters = parseAbilityParameters(s);
            steps.add(new AbilityStepDefinition(ability, parameters, delayTicks));
        }
        return List.copyOf(steps);
    }

    private static String requireString(JsonObject obj, String key) {
        if (!obj.has(key)) throw new IllegalArgumentException("Missing required field: " + key);
        return obj.get(key).getAsString();
    }

    private static String getString(JsonObject obj, String key, String fallback) {
        return obj.has(key) ? obj.get(key).getAsString() : fallback;
    }

    private static double getDouble(JsonObject obj, String key, double fallback) {
        return obj.has(key) ? obj.get(key).getAsDouble() : fallback;
    }

    private static boolean getBoolean(JsonObject obj, String key, boolean fallback) {
        return obj.has(key) ? obj.get(key).getAsBoolean() : fallback;
    }
}
