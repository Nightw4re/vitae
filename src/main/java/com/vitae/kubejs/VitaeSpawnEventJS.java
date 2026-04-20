package com.vitae.kubejs;

import dev.latvian.mods.kubejs.event.KubeEvent;

/** KubeJS event fired when a Vitae entity spawns. */
public record VitaeSpawnEventJS(String entityId) implements KubeEvent {}
