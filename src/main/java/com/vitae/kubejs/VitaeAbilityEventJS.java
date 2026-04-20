package com.vitae.kubejs;

import com.vitae.data.AbilityDefinition;
import dev.latvian.mods.kubejs.event.KubeEvent;

/** KubeJS event fired when a Vitae entity uses an ability. */
public record VitaeAbilityEventJS(String entityId, AbilityDefinition ability) implements KubeEvent {}
