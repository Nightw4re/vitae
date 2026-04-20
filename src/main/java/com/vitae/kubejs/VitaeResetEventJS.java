package com.vitae.kubejs;

import dev.latvian.mods.kubejs.event.KubeEvent;

/** KubeJS event fired when a Vitae boss resets after losing aggro. */
public record VitaeResetEventJS(String entityId) implements KubeEvent {}
