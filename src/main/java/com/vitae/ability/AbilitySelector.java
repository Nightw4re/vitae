package com.vitae.ability;

import com.vitae.data.AbilityCondition;
import com.vitae.data.AbilityDefinition;

import java.util.List;
import java.util.Random;

/**
 * Selects which ability to use given the current entity state.
 *
 * <p>Pure Java — no Minecraft dependency.
 */
public final class AbilitySelector {

    private AbilitySelector() {}

    /**
     * Returns the first ability from {@code candidates} that is ready and whose conditions
     * are met, or {@code null} if none qualifies.
     *
     * <p>Candidates are checked in order. Chance is evaluated per-candidate with the
     * provided {@link Random}.
     *
     * @param candidates    ability definitions available this phase (ordered by priority)
     * @param cooldowns     current cooldown state
     * @param distanceToTarget distance from the entity to its target in blocks
     * @param healthPercent current health as a fraction of max health (0.0–1.0)
     * @param random        RNG source
     */
    public static AbilityDefinition select(
            List<AbilityDefinition> candidates,
            AbilityCooldownTracker cooldowns,
            double distanceToTarget,
            double healthPercent,
            Random random
    ) {
        for (AbilityDefinition ability : candidates) {
            if (!cooldowns.isReady(ability.id())) continue;

            AbilityCondition condition = ability.condition();
            if (condition != null) {
                if (!condition.matchesRange(distanceToTarget)) continue;
                if (!condition.matchesHealth(healthPercent)) continue;
                if (condition.chance() <= 0.0 || random.nextDouble() >= condition.chance()) continue;
            }

            return ability;
        }
        return null;
    }
}
