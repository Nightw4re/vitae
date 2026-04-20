package com.vitae.data;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Full dialogue tree for a Vitae NPC.
 *
 * @param rootId  ID of the first node to show when a player starts the conversation
 * @param nodes   all dialogue nodes keyed by ID
 */
public record DialogueDefinition(
        String rootId,
        Map<String, DialogueNode> nodes
) {
    public static DialogueDefinition of(String rootId, List<DialogueNode> nodeList) {
        Map<String, DialogueNode> map = nodeList.stream()
                .collect(Collectors.toUnmodifiableMap(DialogueNode::id, Function.identity()));
        return new DialogueDefinition(rootId, map);
    }

    /** Returns the root node, or null if rootId is not found. */
    public DialogueNode getRoot() {
        return nodes.get(rootId);
    }

    /** Returns the node with the given ID, or null if not found. */
    public DialogueNode getNode(String id) {
        return nodes.get(id);
    }
}
