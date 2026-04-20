package com.vitae.ability;

import com.vitae.testsupport.TestAssertions;

public final class AbilityCooldownTrackerTest {

    public static void run() {
        testReadyByDefault();
        testNotReadyAfterStart();
        testReadyAfterCooldownExpires();
        testTickDecrements();
        testResetAll();
        testRemainingAfterPartialTick();
    }

    private static void testReadyByDefault() {
        AbilityCooldownTracker tracker = new AbilityCooldownTracker();
        TestAssertions.assertTrue(tracker.isReady("melee_attack"));
    }

    private static void testNotReadyAfterStart() {
        AbilityCooldownTracker tracker = new AbilityCooldownTracker();
        tracker.startCooldown("melee_attack", 20);
        TestAssertions.assertFalse(tracker.isReady("melee_attack"));
        TestAssertions.assertEquals(20, tracker.getRemaining("melee_attack"));
    }

    private static void testReadyAfterCooldownExpires() {
        AbilityCooldownTracker tracker = new AbilityCooldownTracker();
        tracker.startCooldown("staff_beam", 3);
        tracker.tick();
        tracker.tick();
        tracker.tick();
        TestAssertions.assertTrue(tracker.isReady("staff_beam"));
        TestAssertions.assertEquals(0, tracker.getRemaining("staff_beam"));
    }

    private static void testTickDecrements() {
        AbilityCooldownTracker tracker = new AbilityCooldownTracker();
        tracker.startCooldown("dash", 10);
        tracker.tick();
        TestAssertions.assertEquals(9, tracker.getRemaining("dash"));
    }

    private static void testResetAll() {
        AbilityCooldownTracker tracker = new AbilityCooldownTracker();
        tracker.startCooldown("melee_attack", 20);
        tracker.startCooldown("aoe", 40);
        tracker.resetAll();
        TestAssertions.assertTrue(tracker.isReady("melee_attack"));
        TestAssertions.assertTrue(tracker.isReady("aoe"));
    }

    private static void testRemainingAfterPartialTick() {
        AbilityCooldownTracker tracker = new AbilityCooldownTracker();
        tracker.startCooldown("summon", 5);
        tracker.tick();
        tracker.tick();
        TestAssertions.assertEquals(3, tracker.getRemaining("summon"));
        TestAssertions.assertFalse(tracker.isReady("summon"));
    }
}
