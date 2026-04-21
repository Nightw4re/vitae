package com.vitae.phase;

import com.vitae.data.AttributeDefinition;
import com.vitae.data.AbilityReference;
import com.vitae.data.CombatDefinition;
import com.vitae.data.EntityDefinition;
import com.vitae.data.EquipmentDefinition;
import com.vitae.data.PhaseDefinition;
import com.vitae.data.PhaseTransitionDefinition;
import com.vitae.data.SpawnRules;
import com.vitae.testsupport.TestAssertions;

import java.util.List;

public final class PhaseManagerTest {

    public static void run() {
        testStartsInPhase1();
        testTransitionToPhase2OnHealthDrop();
        testNoTransitionWhileHealthAboveThreshold();
        testNoTransitionResultWhenPhaseUnchanged();
        testInTransitionBlocksFurtherTransitions();
        testCompleteTransitionAllowsNext();
        testResetGoesBackToPhase1();
        testTransitionSetsFlagWhenPhaseHasTransition();
        testTransitionDoesNotSetFlagWhenNoTransitionDefined();
    }

    private static void testStartsInPhase1() {
        PhaseManager pm = new PhaseManager(twoPhaseEntity());
        TestAssertions.assertEquals("phase_1", pm.getCurrentPhase().id());
    }

    private static void testTransitionToPhase2OnHealthDrop() {
        PhaseManager pm = new PhaseManager(twoPhaseEntity());
        PhaseTransitionResult result = pm.update(0.4);
        TestAssertions.assertNotNull(result);
        TestAssertions.assertEquals("phase_1", result.previous().id());
        TestAssertions.assertEquals("phase_2", result.next().id());
        TestAssertions.assertEquals("phase_2", pm.getCurrentPhase().id());
    }

    private static void testNoTransitionWhileHealthAboveThreshold() {
        PhaseManager pm = new PhaseManager(twoPhaseEntity());
        PhaseTransitionResult result = pm.update(0.8);
        TestAssertions.assertEquals(null, result);
        TestAssertions.assertEquals("phase_1", pm.getCurrentPhase().id());
    }

    private static void testNoTransitionResultWhenPhaseUnchanged() {
        PhaseManager pm = new PhaseManager(twoPhaseEntity());
        pm.update(0.4); // transition to phase_2
        pm.completeTransition();
        PhaseTransitionResult result = pm.update(0.3); // still in phase_2
        TestAssertions.assertEquals(null, result);
    }

    private static void testInTransitionBlocksFurtherTransitions() {
        PhaseManager pm = new PhaseManager(threePhaseEntity());
        pm.update(0.4); // phase_1 -> phase_2 (has transition)
        TestAssertions.assertTrue(pm.isInTransition());
        PhaseTransitionResult blocked = pm.update(0.1); // would be phase_3
        TestAssertions.assertEquals(null, blocked);
        TestAssertions.assertEquals("phase_2", pm.getCurrentPhase().id());
    }

    private static void testCompleteTransitionAllowsNext() {
        PhaseManager pm = new PhaseManager(threePhaseEntity());
        pm.update(0.4); // phase_1 -> phase_2
        pm.completeTransition();
        TestAssertions.assertFalse(pm.isInTransition());
        PhaseTransitionResult result = pm.update(0.1); // phase_2 -> phase_3
        TestAssertions.assertNotNull(result);
        TestAssertions.assertEquals("phase_3", result.next().id());
    }

    private static void testResetGoesBackToPhase1() {
        PhaseManager pm = new PhaseManager(twoPhaseEntity());
        pm.update(0.4);
        pm.completeTransition();
        pm.reset();
        TestAssertions.assertEquals("phase_1", pm.getCurrentPhase().id());
        TestAssertions.assertFalse(pm.isInTransition());
    }

    private static void testTransitionSetsFlagWhenPhaseHasTransition() {
        PhaseManager pm = new PhaseManager(twoPhaseEntity());
        pm.update(0.4); // phase_2 has a transition defined
        TestAssertions.assertTrue(pm.isInTransition());
    }

    private static void testTransitionDoesNotSetFlagWhenNoTransitionDefined() {
        PhaseManager pm = new PhaseManager(twoPhaseEntityNoTransition());
        pm.update(0.4);
        TestAssertions.assertFalse(pm.isInTransition());
    }

    // --- Helpers ---

    private static EntityDefinition twoPhaseEntity() {
        PhaseTransitionDefinition transition = new PhaseTransitionDefinition("phase_2_transition", true, -1);
        List<PhaseDefinition> phases = List.of(
                new PhaseDefinition("phase_1", 1.0, List.of(new AbilityReference("melee_attack", null)), "phase_1_idle", null, 1.0, null),
                new PhaseDefinition("phase_2", 0.5, List.of(new AbilityReference("staff_beam", null)), "phase_2_idle", null, 1.4, transition)
        );
        return new EntityDefinition("model", "animations", AttributeDefinition.defaults(), phases, List.of(), 0, null, null, null, null, null, CombatDefinition.defaults(), EquipmentDefinition.defaults(), null, SpawnRules.defaults());
    }

    private static EntityDefinition twoPhaseEntityNoTransition() {
        List<PhaseDefinition> phases = List.of(
                new PhaseDefinition("phase_1", 1.0, List.of(), null, null, 1.0, null),
                new PhaseDefinition("phase_2", 0.5, List.of(), null, null, 1.0, null)
        );
        return new EntityDefinition("model", "animations", AttributeDefinition.defaults(), phases, List.of(), 0, null, null, null, null, null, CombatDefinition.defaults(), EquipmentDefinition.defaults(), null, SpawnRules.defaults());
    }

    private static EntityDefinition threePhaseEntity() {
        PhaseTransitionDefinition transition = new PhaseTransitionDefinition("transition", true, -1);
        List<PhaseDefinition> phases = List.of(
                new PhaseDefinition("phase_1", 1.0, List.of(), null, null, 1.0, null),
                new PhaseDefinition("phase_2", 0.5, List.of(), null, null, 1.0, transition),
                new PhaseDefinition("phase_3", 0.2, List.of(), null, null, 1.0, null)
        );
        return new EntityDefinition("model", "animations", AttributeDefinition.defaults(), phases, List.of(), 0, null, null, null, null, null, CombatDefinition.defaults(), EquipmentDefinition.defaults(), null, SpawnRules.defaults());
    }
}
