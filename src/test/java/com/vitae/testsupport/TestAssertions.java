package com.vitae.testsupport;

public final class TestAssertions {
    private TestAssertions() {}

    public static void assertEquals(Object expected, Object actual) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }

    public static void assertTrue(boolean value) {
        if (!value) {
            throw new AssertionError("Expected true");
        }
    }

    public static void assertFalse(boolean value) {
        if (value) {
            throw new AssertionError("Expected false");
        }
    }

    public static void assertNotNull(Object value) {
        if (value == null) {
            throw new AssertionError("Expected non-null value");
        }
    }

    public static void assertThrows(Class<? extends Throwable> type, ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable t) {
            if (type.isInstance(t)) {
                return;
            }
            throw new AssertionError("Expected " + type.getName() + " but got " + t.getClass().getName(), t);
        }
        throw new AssertionError("Expected " + type.getName() + " to be thrown");
    }

    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Exception;
    }
}
