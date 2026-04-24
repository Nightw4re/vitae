package com.vitae.data;

import com.vitae.testsupport.TestAssertions;

import java.util.List;

public final class SpawnFormationUtilTest {

    public static void run() {
        testExplicitPointsAreUsedAsProvided();
        testExplicitPointsCycleWhenMoreMobsThanPoints();
        testFourMobFallbackUsesSquare();
        testFallbackUsesCircleWhenNoExplicitPoints();
    }

    private static void testExplicitPointsAreUsedAsProvided() {
        List<SpawnPointDefinition> points = List.of(
                new SpawnPointDefinition(2.0, 2.0, 0.0),
                new SpawnPointDefinition(-2.0, 2.0, 0.0),
                new SpawnPointDefinition(-2.0, -2.0, 0.0),
                new SpawnPointDefinition(2.0, -2.0, 0.0)
        );

        TestAssertions.assertEquals(2.0, SpawnFormationUtil.resolvePoint(points, 2.0, 4, 0).x());
        TestAssertions.assertEquals(-2.0, SpawnFormationUtil.resolvePoint(points, 2.0, 4, 1).x());
        TestAssertions.assertEquals(-2.0, SpawnFormationUtil.resolvePoint(points, 2.0, 4, 2).x());
        TestAssertions.assertEquals(2.0, SpawnFormationUtil.resolvePoint(points, 2.0, 4, 3).x());
    }

    private static void testExplicitPointsCycleWhenMoreMobsThanPoints() {
        List<SpawnPointDefinition> points = List.of(
                new SpawnPointDefinition(1.0, 1.0, 0.0),
                new SpawnPointDefinition(-1.0, 1.0, 0.0)
        );

        TestAssertions.assertEquals(1.0, SpawnFormationUtil.resolvePoint(points, 2.0, 8, 0).x());
        TestAssertions.assertEquals(-1.0, SpawnFormationUtil.resolvePoint(points, 2.0, 8, 1).x());
        TestAssertions.assertEquals(1.0, SpawnFormationUtil.resolvePoint(points, 2.0, 8, 2).x());
        TestAssertions.assertEquals(-1.0, SpawnFormationUtil.resolvePoint(points, 2.0, 8, 3).x());
    }

    private static void testFourMobFallbackUsesSquare() {
        TestAssertions.assertEquals(2.0, SpawnFormationUtil.resolvePoint(List.of(), 2.0, 4, 0).x());
        TestAssertions.assertEquals(-2.0, SpawnFormationUtil.resolvePoint(List.of(), 2.0, 4, 1).x());
        TestAssertions.assertEquals(-2.0, SpawnFormationUtil.resolvePoint(List.of(), 2.0, 4, 2).x());
        TestAssertions.assertEquals(2.0, SpawnFormationUtil.resolvePoint(List.of(), 2.0, 4, 3).x());
    }

    private static void testFallbackUsesCircleWhenNoExplicitPoints() {
        SpawnPointDefinition point = SpawnFormationUtil.resolvePoint(List.of(), 2.0, 8, 0);
        TestAssertions.assertEquals(2.0, Math.round(point.x() * 10.0) / 10.0);
        TestAssertions.assertEquals(0.0, Math.round(point.z() * 10.0) / 10.0);
    }
}
