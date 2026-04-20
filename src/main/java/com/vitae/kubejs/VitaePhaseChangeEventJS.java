package com.vitae.kubejs;

import com.vitae.data.PhaseDefinition;
import dev.latvian.mods.kubejs.event.KubeEvent;

/** KubeJS event fired when a Vitae boss changes phase. */
public record VitaePhaseChangeEventJS(
        String entityId,
        PhaseDefinition previous,
        PhaseDefinition next
) implements KubeEvent {}
