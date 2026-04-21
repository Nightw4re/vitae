package com.vitae.data;

import com.vitae.testsupport.TestAssertions;

public final class GrabTimelineTest {

    public static void run() {
        testProgressClampsLow();
        testProgressClampsHigh();
        testTargetYMovesLinearly();
        testShouldReleaseAtDuration();
    }

    private static void testProgressClampsLow() {
        TestAssertions.assertEquals(0.0, GrabTimeline.progress(-5, 200));
    }

    private static void testProgressClampsHigh() {
        TestAssertions.assertEquals(1.0, GrabTimeline.progress(250, 200));
    }

    private static void testTargetYMovesLinearly() {
        TestAssertions.assertEquals(14.0, GrabTimeline.targetY(10.0, 8.0, 100, 200));
    }

    private static void testShouldReleaseAtDuration() {
        TestAssertions.assertFalse(GrabTimeline.shouldRelease(199, 200));
        TestAssertions.assertTrue(GrabTimeline.shouldRelease(200, 200));
    }
}
