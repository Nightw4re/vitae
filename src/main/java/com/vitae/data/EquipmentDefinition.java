package com.vitae.data;

public record EquipmentDefinition(
        String mainHandItem
) {
    public static EquipmentDefinition defaults() {
        return new EquipmentDefinition(null);
    }
}
