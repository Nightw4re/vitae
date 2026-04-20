package com.vitae.data;

import java.util.List;

/**
 * Immutable data model for a Vitae NPC loaded from a datapack JSON file.
 *
 * <p>An NPC definition lives at {@code data/<namespace>/vitae/npcs/<name>.json}.
 * NPCs are always friendly — for hostile entities use {@link EntityDefinition}.
 *
 * @param model     GeckoLib model resource location
 * @param animations GeckoLib animation file resource location
 * @param dialogue  optional dialogue tree (nullable = no conversation)
 * @param trades    list of trades offered (empty = no trading)
 * @param behavior  movement and guard behavior
 */
public record NpcDefinition(
        String model,
        String animations,
        DialogueDefinition dialogue,
        List<TradeEntry> trades,
        NpcBehavior behavior
) {
    public boolean hasDialogue() {
        return dialogue != null && dialogue.getRoot() != null;
    }

    public boolean hasTrades() {
        return trades != null && !trades.isEmpty();
    }
}
