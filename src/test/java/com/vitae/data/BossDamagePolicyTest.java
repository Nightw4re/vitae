package com.vitae.data;

import com.vitae.testsupport.TestAssertions;

public final class BossDamagePolicyTest {

    public static void run() {
        testClampsDamageToFloorWhenAboveFloor();
        testBlocksDamageAtFloor();
        testLeavesDamageUntouchedWithoutFloor();
        testBlocksDamageDuringSummonLock();
    }

    private static void testClampsDamageToFloorWhenAboveFloor() {
        float applied = BossDamagePolicy.clampDamageToFloor(100.0F, 100.0F, 0.75D, 60.0F);
        TestAssertions.assertEquals(25.0F, applied);
    }

    private static void testBlocksDamageAtFloor() {
        float applied = BossDamagePolicy.clampDamageToFloor(75.0F, 100.0F, 0.75D, 10.0F);
        TestAssertions.assertEquals(0.0F, applied);
    }

    private static void testLeavesDamageUntouchedWithoutFloor() {
        float applied = BossDamagePolicy.clampDamageToFloor(100.0F, 100.0F, 0.0D, 60.0F);
        TestAssertions.assertEquals(60.0F, applied);
    }

    private static void testBlocksDamageDuringSummonLock() {
        TestAssertions.assertTrue(BossDamagePolicy.shouldBlockDamageWhileSummonLocked(true));
        TestAssertions.assertFalse(BossDamagePolicy.shouldBlockDamageWhileSummonLocked(false));
    }
}
