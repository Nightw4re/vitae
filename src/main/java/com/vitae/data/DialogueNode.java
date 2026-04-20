package com.vitae.data;

import java.util.List;

/**
 * A node in a dialogue tree. Each node has one or more response options the player can pick.
 *
 * @param id       unique identifier for this node within the dialogue
 * @param npcText  text spoken by the NPC when this node is entered
 * @param options  player response options (empty = auto-close after npcText)
 */
public record DialogueNode(
        String id,
        String npcText,
        List<DialogueLine> options
) {
    /** Returns true if this node ends the conversation (no player options). */
    public boolean isTerminal() {
        return options == null || options.isEmpty();
    }
}
