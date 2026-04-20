package com.vitae.api;

import com.vitae.data.AbilityDefinition;
import com.vitae.data.PhaseDefinition;

/**
 * Events fired by Vitae entities that KubeJS scripts can listen to.
 *
 * <p>Each event carries the entity ID and relevant context. KubeJS binds
 * these via {@code VitaeEvents.onSpawn(...)}, {@code VitaeEvents.onDeath(...)}, etc.
 */
public sealed interface VitaeEntityEvent {

    /** Unique ID of the entity definition that fired this event. */
    String entityId();

    /** Fired when a Vitae entity spawns into the world. */
    record Spawn(String entityId) implements VitaeEntityEvent {}

    /** Fired when a Vitae entity dies. */
    record Death(String entityId, boolean becameFriendly) implements VitaeEntityEvent {}

    /** Fired when a Vitae boss transitions to a new phase. */
    record PhaseChange(String entityId, PhaseDefinition previous, PhaseDefinition next)
            implements VitaeEntityEvent {}

    /** Fired when a Vitae entity uses an ability. */
    record AbilityUsed(String entityId, AbilityDefinition ability)
            implements VitaeEntityEvent {}

    /** Fired when a Vitae boss resets (lost aggro). */
    record Reset(String entityId) implements VitaeEntityEvent {}
}
