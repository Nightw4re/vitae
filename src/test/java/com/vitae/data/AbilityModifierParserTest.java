package com.vitae.data;

import com.vitae.testsupport.TestAssertions;

public final class AbilityModifierParserTest {

    public static void run() {
        testAbsoluteCooldown();
        testRelativeCooldownMultiplier();
        testRelativeCooldownAdd();
        testInvalidModifierFallsBack();
        testBlankModifierFallsBack();
    }

    private static void testAbsoluteCooldown() {
        TestAssertions.assertEquals(200, AbilityModifierParser.parseCooldownTicks("200", 600));
    }

    private static void testRelativeCooldownMultiplier() {
        TestAssertions.assertEquals(540, AbilityModifierParser.parseCooldownTicks("*0.9", 600));
    }

    private static void testRelativeCooldownAdd() {
        TestAssertions.assertEquals(720, AbilityModifierParser.parseCooldownTicks("+120", 600));
    }

    private static void testInvalidModifierFallsBack() {
        TestAssertions.assertEquals(600, AbilityModifierParser.parseCooldownTicks("not-a-number", 600));
    }

    private static void testBlankModifierFallsBack() {
        TestAssertions.assertEquals(600, AbilityModifierParser.parseCooldownTicks("   ", 600));
    }
}
