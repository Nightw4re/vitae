package com.vitae.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses a Vitae NPC JSON definition into a {@link NpcDefinition}.
 *
 * <p>NPC definitions live at {@code data/<namespace>/vitae/npcs/<name>.json}.
 */
public final class NpcDefinitionLoader {

    private NpcDefinitionLoader() {}

    public static NpcDefinition parse(String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();

        String model      = requireString(root, "model");
        String animations = requireString(root, "animations");

        DialogueDefinition dialogue = parseDialogue(root);
        List<TradeEntry>   trades   = parseTrades(root);
        NpcBehavior        behavior = parseBehavior(root);

        return new NpcDefinition(model, animations, dialogue, trades, behavior);
    }

    private static DialogueDefinition parseDialogue(JsonObject root) {
        if (!root.has("dialogue")) return null;
        JsonObject d    = root.getAsJsonObject("dialogue");
        String rootId   = requireString(d, "root");
        JsonArray nodes = d.getAsJsonArray("nodes");

        List<DialogueNode> nodeList = new ArrayList<>();
        for (JsonElement el : nodes) {
            JsonObject n    = el.getAsJsonObject();
            String id       = requireString(n, "id");
            String npcText  = requireString(n, "text");
            List<DialogueLine> options = new ArrayList<>();
            if (n.has("options")) {
                for (JsonElement opt : n.getAsJsonArray("options")) {
                    JsonObject o  = opt.getAsJsonObject();
                    String text   = requireString(o, "text");
                    String cond   = getString(o, "condition", null);
                    String nextId = getString(o, "next", null);
                    options.add(new DialogueLine(text, cond, nextId));
                }
            }
            nodeList.add(new DialogueNode(id, npcText, List.copyOf(options)));
        }
        return DialogueDefinition.of(rootId, nodeList);
    }

    private static List<TradeEntry> parseTrades(JsonObject root) {
        if (!root.has("trades")) return List.of();
        List<TradeEntry> trades = new ArrayList<>();
        for (JsonElement el : root.getAsJsonArray("trades")) {
            JsonObject t = el.getAsJsonObject();
            trades.add(new TradeEntry(
                    requireString(t, "cost_a"),
                    (int) getDouble(t, "cost_a_count", 1),
                    getString(t, "cost_b", null),
                    (int) getDouble(t, "cost_b_count", 1),
                    requireString(t, "result"),
                    (int) getDouble(t, "result_count", 1),
                    (int) getDouble(t, "max_uses", -1)
            ));
        }
        return List.copyOf(trades);
    }

    private static NpcBehavior parseBehavior(JsonObject root) {
        if (!root.has("behavior")) return NpcBehavior.defaults();
        JsonObject b = root.getAsJsonObject("behavior");
        return new NpcBehavior(
                getString(b, "type", NpcBehavior.IDLE),
                getDouble(b, "follow_range", 16.0),
                getDouble(b, "guard_radius", 8.0)
        );
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
}
