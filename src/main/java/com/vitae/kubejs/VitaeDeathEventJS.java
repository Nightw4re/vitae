package com.vitae.kubejs;

import dev.latvian.mods.kubejs.event.KubeEvent;

/** KubeJS event fired when a Vitae entity dies. */
public record VitaeDeathEventJS(String entityId, boolean becameFriendly) implements KubeEvent {}
