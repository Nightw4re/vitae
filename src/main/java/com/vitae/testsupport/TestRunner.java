package com.vitae.testsupport;

public final class TestRunner {
    private static final String[] TESTS = {
            "com.vitae.data.EntityDefinitionTest",
            "com.vitae.phase.PhaseManagerTest",
            "com.vitae.ability.AbilityCooldownTrackerTest",
            "com.vitae.ability.AbilitySelectorTest",
            "com.vitae.api.VitaeEventBusTest",
            "com.vitae.data.NpcDefinitionTest",
    };

    public static void main(String[] args) throws Exception {
        if (TESTS.length == 0) {
            System.out.println("No tests registered.");
            return;
        }
        int passed = 0;
        for (String test : TESTS) {
            Class<?> clazz = Class.forName(test);
            clazz.getMethod("run").invoke(null);
            passed++;
            System.out.println("PASS " + test);
        }
        System.out.println("Executed " + passed + " tests");
    }
}
