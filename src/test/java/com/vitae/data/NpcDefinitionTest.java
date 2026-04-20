package com.vitae.data;

import com.vitae.testsupport.TestAssertions;

import java.util.List;

public final class NpcDefinitionTest {

    public static void run() {
        testHasDialogueWhenDefined();
        testNoDialogueWhenNull();
        testHasTradesWhenNonEmpty();
        testNoTradesWhenEmpty();
        testDialogueTreeNavigation();
        testDialogueTerminalNode();
        testNpcBehaviorDefaults();
        testNpcBehaviorFlags();
    }

    private static void testHasDialogueWhenDefined() {
        NpcDefinition npc = npcWithDialogue();
        TestAssertions.assertTrue(npc.hasDialogue());
    }

    private static void testNoDialogueWhenNull() {
        NpcDefinition npc = new NpcDefinition("model", "animations", null, List.of(), NpcBehavior.defaults());
        TestAssertions.assertFalse(npc.hasDialogue());
    }

    private static void testHasTradesWhenNonEmpty() {
        NpcDefinition npc = npcWithTrades();
        TestAssertions.assertTrue(npc.hasTrades());
    }

    private static void testNoTradesWhenEmpty() {
        NpcDefinition npc = new NpcDefinition("model", "animations", null, List.of(), NpcBehavior.defaults());
        TestAssertions.assertFalse(npc.hasTrades());
    }

    private static void testDialogueTreeNavigation() {
        DialogueDefinition dialogue = sampleDialogue();
        DialogueNode root = dialogue.getRoot();
        TestAssertions.assertNotNull(root);
        TestAssertions.assertEquals("greeting", root.id());
        // follow first option to next node
        String nextId = root.options().get(0).nextId();
        DialogueNode next = dialogue.getNode(nextId);
        TestAssertions.assertNotNull(next);
        TestAssertions.assertEquals("farewell", next.id());
    }

    private static void testDialogueTerminalNode() {
        DialogueDefinition dialogue = sampleDialogue();
        DialogueNode farewell = dialogue.getNode("farewell");
        TestAssertions.assertNotNull(farewell);
        TestAssertions.assertTrue(farewell.isTerminal());
    }

    private static void testNpcBehaviorDefaults() {
        NpcBehavior b = NpcBehavior.defaults();
        TestAssertions.assertEquals(NpcBehavior.IDLE, b.type());
        TestAssertions.assertFalse(b.isFollow());
        TestAssertions.assertFalse(b.isGuard());
    }

    private static void testNpcBehaviorFlags() {
        NpcBehavior follow = new NpcBehavior(NpcBehavior.FOLLOW, 20.0, 0.0);
        TestAssertions.assertTrue(follow.isFollow());
        TestAssertions.assertFalse(follow.isGuard());

        NpcBehavior guard = new NpcBehavior(NpcBehavior.GUARD, 0.0, 12.0);
        TestAssertions.assertFalse(guard.isFollow());
        TestAssertions.assertTrue(guard.isGuard());
    }

    // --- Helpers ---

    private static DialogueDefinition sampleDialogue() {
        DialogueNode farewell = new DialogueNode("farewell", "May the stars guide you.", List.of());
        DialogueNode greeting = new DialogueNode("greeting", "Greetings, traveler.",
                List.of(new DialogueLine("Farewell.", null, "farewell")));
        return DialogueDefinition.of("greeting", List.of(greeting, farewell));
    }

    private static NpcDefinition npcWithDialogue() {
        return new NpcDefinition("model", "animations", sampleDialogue(), List.of(), NpcBehavior.defaults());
    }

    private static NpcDefinition npcWithTrades() {
        TradeEntry trade = new TradeEntry("minecraft:emerald", 5, null, 0, "mypack:naquadah_ingot", 1, 10);
        return new NpcDefinition("model", "animations", null, List.of(trade), NpcBehavior.defaults());
    }
}
