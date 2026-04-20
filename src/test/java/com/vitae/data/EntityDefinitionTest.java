package com.vitae.data;

import com.vitae.testsupport.TestAssertions;

import java.util.List;

public final class EntityDefinitionTest {

    public static void run() {
        testIsBossWithMultiplePhases();
        testIsBossWithSinglePhase();
        testIsBossWithNoPhases();
        testGetPhaseForHealthPhase1();
        testGetPhaseForHealthPhase2();
        testGetPhaseForHealthAtExactThreshold();
        testGetPhaseForHealthNoPhases();
        testDefaultAttributes();
    }

    private static void testIsBossWithMultiplePhases() {
        EntityDefinition def = twoPhaseEntity();
        TestAssertions.assertTrue(def.isBoss());
    }

    private static void testIsBossWithSinglePhase() {
        EntityDefinition def = singlePhaseEntity();
        TestAssertions.assertFalse(def.isBoss());
    }

    private static void testIsBossWithNoPhases() {
        EntityDefinition def = noPhasesEntity();
        TestAssertions.assertFalse(def.isBoss());
    }

    private static void testGetPhaseForHealthPhase1() {
        // At 80% health, phase_1 (threshold 1.0) should be active
        EntityDefinition def = twoPhaseEntity();
        PhaseDefinition phase = def.getPhaseForHealth(0.8);
        TestAssertions.assertNotNull(phase);
        TestAssertions.assertEquals("phase_1", phase.id());
    }

    private static void testGetPhaseForHealthPhase2() {
        // At 40% health, phase_2 (threshold 0.5) should be active
        EntityDefinition def = twoPhaseEntity();
        PhaseDefinition phase = def.getPhaseForHealth(0.4);
        TestAssertions.assertNotNull(phase);
        TestAssertions.assertEquals("phase_2", phase.id());
    }

    private static void testGetPhaseForHealthAtExactThreshold() {
        // At exactly 50% health, phase_2 should activate
        EntityDefinition def = twoPhaseEntity();
        PhaseDefinition phase = def.getPhaseForHealth(0.5);
        TestAssertions.assertNotNull(phase);
        TestAssertions.assertEquals("phase_2", phase.id());
    }

    private static void testGetPhaseForHealthNoPhases() {
        EntityDefinition def = noPhasesEntity();
        PhaseDefinition phase = def.getPhaseForHealth(0.5);
        TestAssertions.assertEquals(null, phase);
    }

    private static void testDefaultAttributes() {
        AttributeDefinition defaults = AttributeDefinition.defaults();
        TestAssertions.assertEquals(20.0, defaults.maxHealth());
        TestAssertions.assertEquals(0.25, defaults.movementSpeed());
        TestAssertions.assertEquals(2.0, defaults.attackDamage());
        TestAssertions.assertEquals(0.0, defaults.armor());
    }

    // --- Helpers ---

    private static EntityDefinition twoPhaseEntity() {
        List<PhaseDefinition> phases = List.of(
                new PhaseDefinition("phase_1", 1.0, List.of("melee_attack", "summon"), null, null, 1.0, null),
                new PhaseDefinition("phase_2", 0.5, List.of("staff_beam", "melee_attack"), null, null, 1.0, null)
        );
        return new EntityDefinition(
                "mypack:geo/test.geo.json",
                "mypack:animations/test.animation.json",
                new AttributeDefinition(200.0, 0.3, 12.0, 0.0),
                phases,
                "mypack:entities/test",
                null, null, null, null, null
        );
    }

    private static EntityDefinition singlePhaseEntity() {
        return new EntityDefinition(
                "mypack:geo/test.geo.json",
                "mypack:animations/test.animation.json",
                AttributeDefinition.defaults(),
                List.of(new PhaseDefinition("phase_1", 1.0, List.of(), null, null, 1.0, null)),
                null, null, null, null, null, null
        );
    }

    private static EntityDefinition noPhasesEntity() {
        return new EntityDefinition(
                "mypack:geo/test.geo.json",
                "mypack:animations/test.animation.json",
                AttributeDefinition.defaults(),
                List.of(),
                null, null, null, null, null, null
        );
    }
}
