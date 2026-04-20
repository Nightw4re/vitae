package com.vitae.phase;

import com.vitae.data.PhaseDefinition;

/**
 * Describes the result of a phase change — which phase was left and which was entered.
 */
public record PhaseTransitionResult(
        PhaseDefinition previous,
        PhaseDefinition next
) {}
