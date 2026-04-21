package com.vitae.ability;

import com.vitae.data.AbilityCondition;
import com.vitae.data.AbilityDefinition;
import com.vitae.data.AbilityParameters;
import com.vitae.testsupport.TestAssertions;

import java.util.List;
import java.util.Random;

public final class AbilitySelectorTest {

    public static void run() {
        testSelectsFirstReadyAbility();
        testSkipsAbilityOnCooldown();
        testSkipsAbilityOutOfRange();
        testSkipsAbilityHealthConditionNotMet();
        testReturnsNullWhenNoneQualify();
        testAlwaysFailsChanceZero();
        testAlwaysPassesChanceOne();
    }

    private static void testSelectsFirstReadyAbility() {
        AbilityCooldownTracker tracker = new AbilityCooldownTracker();
        List<AbilityDefinition> abilities = List.of(
                ability("melee_attack", 0, null),
                ability("staff_beam", 0, null)
        );
        AbilityDefinition selected = AbilitySelector.select(abilities, tracker, 3.0, 0.8, fixedRandom(0.5));
        TestAssertions.assertEquals("melee_attack", selected.id());
    }

    private static void testSkipsAbilityOnCooldown() {
        AbilityCooldownTracker tracker = new AbilityCooldownTracker();
        tracker.startCooldown("melee_attack", 20);
        List<AbilityDefinition> abilities = List.of(
                ability("melee_attack", 20, null),
                ability("staff_beam", 0, null)
        );
        AbilityDefinition selected = AbilitySelector.select(abilities, tracker, 3.0, 0.8, fixedRandom(0.5));
        TestAssertions.assertEquals("staff_beam", selected.id());
    }

    private static void testSkipsAbilityOutOfRange() {
        AbilityCooldownTracker tracker = new AbilityCooldownTracker();
        AbilityCondition rangeCondition = new AbilityCondition(0.0, 5.0, 0.0, 1.0, 1.0);
        List<AbilityDefinition> abilities = List.of(
                ability("staff_beam", 0, rangeCondition),
                ability("melee_attack", 0, null)
        );
        // distance 10 > maxRange 5, so staff_beam is skipped
        AbilityDefinition selected = AbilitySelector.select(abilities, tracker, 10.0, 0.8, fixedRandom(0.5));
        TestAssertions.assertEquals("melee_attack", selected.id());
    }

    private static void testSkipsAbilityHealthConditionNotMet() {
        AbilityCooldownTracker tracker = new AbilityCooldownTracker();
        // only activates below 50% health
        AbilityCondition healthCondition = new AbilityCondition(0.0, -1.0, 0.0, 0.5, 1.0);
        List<AbilityDefinition> abilities = List.of(
                ability("enrage", 0, healthCondition),
                ability("melee_attack", 0, null)
        );
        // health 80% > maxHealth 50%, enrage skipped
        AbilityDefinition selected = AbilitySelector.select(abilities, tracker, 3.0, 0.8, fixedRandom(0.5));
        TestAssertions.assertEquals("melee_attack", selected.id());
    }

    private static void testReturnsNullWhenNoneQualify() {
        AbilityCooldownTracker tracker = new AbilityCooldownTracker();
        tracker.startCooldown("melee_attack", 10);
        List<AbilityDefinition> abilities = List.of(ability("melee_attack", 10, null));
        AbilityDefinition selected = AbilitySelector.select(abilities, tracker, 3.0, 0.8, fixedRandom(0.5));
        TestAssertions.assertEquals(null, selected);
    }

    private static void testAlwaysFailsChanceZero() {
        AbilityCooldownTracker tracker = new AbilityCooldownTracker();
        AbilityCondition zeroChance = new AbilityCondition(0.0, -1.0, 0.0, 1.0, 0.0);
        List<AbilityDefinition> abilities = List.of(ability("melee_attack", 0, zeroChance));
        AbilityDefinition selected = AbilitySelector.select(abilities, tracker, 3.0, 0.8, fixedRandom(0.0));
        TestAssertions.assertEquals(null, selected);
    }

    private static void testAlwaysPassesChanceOne() {
        AbilityCooldownTracker tracker = new AbilityCooldownTracker();
        AbilityCondition fullChance = new AbilityCondition(0.0, -1.0, 0.0, 1.0, 1.0);
        List<AbilityDefinition> abilities = List.of(ability("melee_attack", 0, fullChance));
        AbilityDefinition selected = AbilitySelector.select(abilities, tracker, 3.0, 0.8, fixedRandom(0.99));
        TestAssertions.assertNotNull(selected);
    }

    // --- Helpers ---

    private static AbilityDefinition ability(String id, int cooldown, AbilityCondition condition) {
        return new AbilityDefinition(id, id, cooldown, condition, AbilityParameters.empty(), List.of(), 0, 0, true);
    }

    private static Random fixedRandom(double value) {
        return new Random() {
            @Override public double nextDouble() { return value; }
        };
    }
}
