package com.vitae.data;

public record AbilityStepDefinition(
        String ability,
        AbilityParameters parameters,
        int delayTicks
) {}
