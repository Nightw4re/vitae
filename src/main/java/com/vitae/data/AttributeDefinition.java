package com.vitae.data;

/**
 * Holds the base attribute values for a Vitae entity as defined in the datapack JSON.
 */
public record AttributeDefinition(
        double maxHealth,
        double movementSpeed,
        double attackDamage,
        double armor
) {

    public static AttributeDefinition defaults() {
        return new AttributeDefinition(20.0, 0.25, 2.0, 0.0);
    }
}
