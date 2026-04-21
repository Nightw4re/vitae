package com.vitae.data;

/** Utility for parsing lightweight numeric modifiers used in JSON references. */
public final class AbilityModifierParser {
    private AbilityModifierParser() {}

    public static int parseCooldownTicks(String modifier, int baseCooldown) {
        if (modifier == null || modifier.isBlank()) {
            return Math.max(0, baseCooldown);
        }

        String trimmed = modifier.trim();
        try {
            if (trimmed.startsWith("*")) {
                double factor = Double.parseDouble(trimmed.substring(1));
                return Math.max(0, (int) Math.round(baseCooldown * factor));
            }
            if (trimmed.startsWith("+") || trimmed.startsWith("-")) {
                int delta = (int) Math.round(Double.parseDouble(trimmed));
                return Math.max(0, baseCooldown + delta);
            }
            if (trimmed.contains(".")) {
                double factor = Double.parseDouble(trimmed);
                return Math.max(0, (int) Math.round(baseCooldown * factor));
            }
            return Math.max(0, Integer.parseInt(trimmed));
        } catch (NumberFormatException ex) {
            return Math.max(0, baseCooldown);
        }
    }
}
