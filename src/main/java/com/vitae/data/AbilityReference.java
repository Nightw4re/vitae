package com.vitae.data;

/**
 * Reference to a reusable ability definition, optionally with local modifiers.
 *
 * <p>Entity and phase JSONs should point at a shared ability by ID and may override
 * numeric fields using lightweight modifiers such as {@code "*0.9"} or {@code "+120"}.
 *
 * @param id ability identifier, typically namespaced like {@code vitae:spin_slash}
 * @param cooldownTicks modifier for cooldown ticks; nullable = use base ability cooldown
 */
public record AbilityReference(
        String id,
        String cooldownTicks
) {}
