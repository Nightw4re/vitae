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
        testDefaultCombat();
        testAbilityReferenceLookupSupportsNamespacedAndShortIds();
        testNaturalSpawnRules();
        testBossIgnoresNaturalSpawnRules();
        testPhaseLockDefinition();
        testMaxPhaseHealthFloor();
        testNextPhaseHealthFloor();
        testHpLockThresholdLookup();
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
        TestAssertions.assertEquals(32.0, defaults.followRange());
        TestAssertions.assertEquals(0.25, defaults.movementSpeed());
        TestAssertions.assertEquals(2.0, defaults.attackDamage());
        TestAssertions.assertEquals(0.0, defaults.armor());
    }

    private static void testDefaultCombat() {
        CombatDefinition defaults = CombatDefinition.defaults();
        TestAssertions.assertTrue(defaults.basicMeleeEnabled());
        TestAssertions.assertEquals(20, defaults.basicMeleeCooldownTicks());
        TestAssertions.assertEquals(3.0, defaults.basicMeleeRange());
        TestAssertions.assertFalse(defaults.scaleDamageWithHeldWeapon());
        TestAssertions.assertFalse(defaults.scaleAttackSpeedWithHeldWeapon());
    }

    private static void testAbilityReferenceLookupSupportsNamespacedAndShortIds() {
        EntityDefinition def = new EntityDefinition(
                "model",
                "animations",
                AttributeDefinition.defaults(),
                List.of(),
                List.of(
                        new AbilityReference("vitae:vex_grab", "200"),
                        new AbilityReference("melee_attack", null)
                ),
                0,
                null,
                null,
                null,
                null,
                null,
                CombatDefinition.defaults(),
                EquipmentDefinition.defaults(),
                List.of(),
                null,
                null,
                SpawnRules.defaults()
        );

        TestAssertions.assertNotNull(def.getAbility("vitae:vex_grab"));
        TestAssertions.assertNotNull(def.getAbility("vex_grab"));
        TestAssertions.assertNotNull(def.getAbility("melee_attack"));
        TestAssertions.assertEquals("200", def.getAbility("vex_grab").cooldownTicks());
    }

    private static void testNaturalSpawnRules() {
        EntityDefinition def = new EntityDefinition(
                "model",
                "animations",
                AttributeDefinition.defaults(),
                List.of(),
                List.of(),
                0,
                null,
                null,
                null,
                null,
                null,
                CombatDefinition.defaults(),
                EquipmentDefinition.defaults(),
                List.of(),
                null,
                null,
                new SpawnRules(List.of("minecraft:plains", "minecraft:forest"), List.of("minecraft:ancient_city"), 4, 2, 1)
        );

        TestAssertions.assertTrue(def.hasNaturalSpawnRestrictions());
        TestAssertions.assertTrue(def.canSpawnInBiome("minecraft:plains"));
        TestAssertions.assertFalse(def.canSpawnInBiome("minecraft:desert"));
        TestAssertions.assertTrue(def.canSpawnInStructure("minecraft:ancient_city"));
        TestAssertions.assertFalse(def.canSpawnInStructure("minecraft:stronghold"));
    }

    private static void testBossIgnoresNaturalSpawnRules() {
        EntityDefinition def = new EntityDefinition(
                "model",
                "animations",
                AttributeDefinition.defaults(),
                List.of(
                        new PhaseDefinition("phase_1", 1.0, 0.0, List.of(), null, null, 1.0, null, null),
                        new PhaseDefinition("phase_2", 0.5, 0.0, List.of(), null, null, 1.0, null, null)
                ),
                List.of(),
                0,
                null,
                null,
                null,
                null,
                null,
                CombatDefinition.defaults(),
                EquipmentDefinition.defaults(),
                List.of(),
                null,
                null,
                new SpawnRules(List.of("minecraft:plains"), List.of("minecraft:ancient_city"), 1, 1, 1)
        );

        TestAssertions.assertTrue(def.isBoss());
        TestAssertions.assertFalse(def.hasNaturalSpawnRestrictions());
    }

    private static void testPhaseLockDefinition() {
        PhaseLockDefinition lock = new PhaseLockDefinition("minecraft:vindicator", 4, true);
        PhaseDefinition phase = new PhaseDefinition("enraged", 0.5, 0.75, List.of(), null, null, 1.0, null, lock);
        TestAssertions.assertNotNull(phase.lock());
        TestAssertions.assertEquals("minecraft:vindicator", phase.lock().summonEntity());
        TestAssertions.assertEquals(4, phase.lock().summonCount());
        TestAssertions.assertTrue(phase.lock().invulnerableWhileSummonsAlive());
        TestAssertions.assertEquals(0.75, phase.healthFloorPercent());
    }

    private static void testMaxPhaseHealthFloor() {
        EntityDefinition def = new EntityDefinition(
                "model",
                "animations",
                AttributeDefinition.defaults(),
                List.of(
                        new PhaseDefinition("normal", 1.0, 0.0, List.of(), null, null, 1.0, null, null),
                        new PhaseDefinition("guarded", 0.75, 0.0, List.of(), null, null, 1.0, null, null),
                        new PhaseDefinition("enraged", 0.5, 0.0, List.of(), null, null, 1.0, null, null)
                ),
                List.of(),
                0,
                null,
                null,
                null,
                null,
                null,
                CombatDefinition.defaults(),
                EquipmentDefinition.defaults(),
                List.of(0.75),
                null,
                null,
                SpawnRules.defaults()
        );

        TestAssertions.assertEquals(0.75, def.maxPhaseHealthFloorOrDefault());
    }

    private static void testNextPhaseHealthFloor() {
        EntityDefinition def = new EntityDefinition(
                "model",
                "animations",
                AttributeDefinition.defaults(),
                List.of(
                        new PhaseDefinition("normal", 1.0, 0.0, List.of(), null, null, 1.0, null, null),
                        new PhaseDefinition("guarded", 0.75, 0.0, List.of(), null, null, 1.0, null, null),
                        new PhaseDefinition("enraged", 0.5, 0.0, List.of(), null, null, 1.0, null, null)
                ),
                List.of(),
                0,
                null,
                null,
                null,
                null,
                null,
                CombatDefinition.defaults(),
                EquipmentDefinition.defaults(),
                List.of(0.75, 0.5),
                null,
                null,
                SpawnRules.defaults()
        );

        TestAssertions.assertEquals(0.75, def.nextPhaseHealthFloorOrDefault(1.0));
        TestAssertions.assertEquals(0.5, def.nextPhaseHealthFloorOrDefault(0.74));
        TestAssertions.assertEquals(0.0, def.nextPhaseHealthFloorOrDefault(0.5));
    }

    private static void testHpLockThresholdLookup() {
        EntityDefinition def = new EntityDefinition(
                "model",
                "animations",
                AttributeDefinition.defaults(),
                List.of(
                        new PhaseDefinition("normal", 1.0, 0.0, List.of(), null, null, 1.0, null, null),
                        new PhaseDefinition("guarded", 0.75, 0.0, List.of(), null, null, 1.0, null, null),
                        new PhaseDefinition("enraged", 0.5, 0.0, List.of(), null, null, 1.0, null, null)
                ),
                List.of(),
                0,
                null,
                null,
                null,
                null,
                null,
                CombatDefinition.defaults(),
                EquipmentDefinition.defaults(),
                List.of(0.75),
                new PhaseLockDefinition("minecraft:zombie", 4, true),
                null,
                SpawnRules.defaults()
        );

        TestAssertions.assertTrue(def.hasHpLockThreshold(0.75));
        TestAssertions.assertFalse(def.hasHpLockThreshold(0.5));
    }

    // --- Helpers ---

    private static EntityDefinition twoPhaseEntity() {
        List<PhaseDefinition> phases = List.of(
                        new PhaseDefinition("phase_1", 1.0, 0.0, List.of(new AbilityReference("melee_attack", null), new AbilityReference("summon", null)), null, null, 1.0, null, null),
                        new PhaseDefinition("phase_2", 0.5, 0.0, List.of(new AbilityReference("staff_beam", null), new AbilityReference("melee_attack", null)), null, null, 1.0, null, null)
        );
        return new EntityDefinition(
                "mypack:geo/test.geo.json",
                "mypack:animations/test.animation.json",
                new AttributeDefinition(200.0, 48.0, 0.3, 12.0, 0.0),
                phases,
                List.of(),
                30,
                "mypack:entities/test",
                null, null, null, null, CombatDefinition.defaults(), EquipmentDefinition.defaults(), List.of(), null, null, SpawnRules.defaults()
        );
    }

    private static EntityDefinition singlePhaseEntity() {
        return new EntityDefinition(
                "mypack:geo/test.geo.json",
                "mypack:animations/test.animation.json",
                AttributeDefinition.defaults(),
                List.of(new PhaseDefinition("phase_1", 1.0, 0.0, List.of(), null, null, 1.0, null, null)),
                List.of(),
                0,
                null, null, null, null, null, CombatDefinition.defaults(), EquipmentDefinition.defaults(), List.of(), null, null, SpawnRules.defaults()
        );
    }

    private static EntityDefinition noPhasesEntity() {
        return new EntityDefinition(
                "mypack:geo/test.geo.json",
                "mypack:animations/test.animation.json",
                AttributeDefinition.defaults(),
                List.of(),
                List.of(),
                0,
                null, null, null, null, null, CombatDefinition.defaults(), EquipmentDefinition.defaults(), List.of(), null, null, SpawnRules.defaults()
        );
    }
}

