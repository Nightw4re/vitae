package com.vitae.data;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Immutable data model for a Vitae entity loaded from a datapack JSON file.
 *
 * <p>An entity definition lives at {@code data/<namespace>/vitae/entities/<name>.json}.
 */
public record EntityDefinition(
        String model,
        String animations,
        AttributeDefinition attributes,
        List<PhaseDefinition> phases,
        List<AbilityReference> abilities,
        int xpReward,
        String lootTable,
        String playerLootTable,
        String genericLootTable,
        String introAnimation,
        DeathBehavior deathBehavior,
        ResetBehavior resetBehavior,
        BossBarDefinition bossBar,
        CombatDefinition combat,
        EquipmentDefinition equipment,
        List<Double> hpLocks,
        PhaseLockDefinition summonLock,
        String spawnStructure,
        SpawnRules spawnRules
) {
    public EntityDefinition(
            String model,
            String animations,
            AttributeDefinition attributes,
            List<PhaseDefinition> phases,
            List<AbilityReference> abilities,
            int xpReward,
            String lootTable,
            String introAnimation,
            DeathBehavior deathBehavior,
            ResetBehavior resetBehavior,
            BossBarDefinition bossBar,
            CombatDefinition combat,
            EquipmentDefinition equipment,
            List<Double> hpLocks,
            PhaseLockDefinition summonLock,
            String spawnStructure,
            SpawnRules spawnRules
    ) {
        this(model, animations, attributes, phases, abilities, xpReward, lootTable, null, null, introAnimation,
                deathBehavior, resetBehavior, bossBar, combat, equipment, hpLocks, summonLock, spawnStructure, spawnRules);
    }

    /**
     * Returns the active phase for the given health percentage (0.0–1.0).
     *
     * <p>Selects the phase with the smallest {@code healthThreshold} that is still
     * {@code >= healthPercent}. Falls back to the last phase if none qualifies.
     */
    public PhaseDefinition getPhaseForHealth(double healthPercent) {
        if (phases == null || phases.isEmpty()) {
            return null;
        }
        return phases.stream()
                .filter(p -> p.healthThreshold() >= healthPercent)
                .min(Comparator.comparingDouble(PhaseDefinition::healthThreshold))
                .orElse(phases.get(phases.size() - 1));
    }

    /** Returns true if this entity has a multi-phase boss setup. */
    public boolean isBoss() {
        return phases != null && phases.size() > 1;
    }

    /** Returns true if this entity has an intro animation. */
    public boolean hasIntro() {
        return introAnimation != null && !introAnimation.isBlank();
    }

    /** Returns true if this entity has a boss bar configured. */
    public boolean hasBossBar() {
        return bossBar != null;
    }

    public int xpRewardOrDefault() {
        return xpReward >= 0 ? xpReward : 0;
    }

    public String lootTableOrDefault() {
        return lootTable;
    }

    public String lootTableForDeath(boolean killedByPlayer) {
        if (killedByPlayer && playerLootTable != null && !playerLootTable.isBlank()) {
            return playerLootTable;
        }
        if (genericLootTable != null && !genericLootTable.isBlank()) {
            return genericLootTable;
        }
        return lootTable;
    }

    public CombatDefinition combatOrDefault() {
        return combat != null ? combat : CombatDefinition.defaults();
    }

    public List<Double> hpLocksOrDefault() {
        return hpLocks != null ? hpLocks : List.of();
    }

    public PhaseLockDefinition summonLockOrDefault() {
        return summonLock;
    }

    public boolean hasHpLockThreshold(double threshold) {
        return hpLocksOrDefault().stream().anyMatch(lock -> Double.compare(lock, threshold) == 0);
    }

    public List<AbilityReference> abilitiesOrDefault() {
        return abilities != null ? abilities : List.of();
    }

    public AbilityReference getAbility(String id) {
        if (id == null || abilities == null) {
            return null;
        }
        return abilities.stream()
                .filter(a -> id.equals(a.id()) || matchesUnqualifiedId(id, a.id()))
                .findFirst()
                .orElse(null);
    }

    public AbilityReference getPhaseAbilityReference(PhaseDefinition phase, String id) {
        if (phase == null || phase.abilities() == null || id == null) {
            return null;
        }
        for (AbilityReference reference : phase.abilities()) {
            if (reference == null || reference.id() == null) {
                continue;
            }
            if (id.equals(reference.id()) || matchesUnqualifiedId(id, reference.id())) {
                return reference;
            }
        }
        return null;
    }

    public EquipmentDefinition equipmentOrDefault() {
        return equipment != null ? equipment : EquipmentDefinition.defaults();
    }

    public SpawnRules spawnRulesOrDefault() {
        return spawnRules != null ? spawnRules : SpawnRules.defaults();
    }

    /** Returns true if this entity is tied to a specific spawn structure. */
    public boolean hasSpawnStructure() {
        return spawnStructure != null && !spawnStructure.isBlank();
    }

    public String spawnStructureOrNull() {
        return hasSpawnStructure() ? spawnStructure : null;
    }

    public boolean hasNaturalSpawnRestrictions() {
        return !isBoss() && (hasSpawnStructure() || spawnRulesOrDefault().hasAnyRestrictions());
    }

    public double maxPhaseHealthFloorOrDefault() {
        List<Double> locks = hpLocksOrDefault();
        if (!locks.isEmpty()) {
            return locks.stream().mapToDouble(Double::doubleValue).max().orElse(0.0D);
        }
        if (phases == null || phases.isEmpty()) {
            return 0.0D;
        }
        return phases.stream()
                .mapToDouble(PhaseDefinition::healthFloorPercent)
                .max()
                .orElse(0.0D);
    }

    public double nextPhaseHealthFloorOrDefault(double currentHealthPercent) {
        List<Double> locks = hpLocksOrDefault();
        if (!locks.isEmpty()) {
            return locks.stream()
                    .filter(lock -> lock < currentHealthPercent)
                    .mapToDouble(Double::doubleValue)
                    .max()
                    .orElse(0.0D);
        }
        if (phases == null || phases.isEmpty()) {
            return 0.0D;
        }
        return phases.stream()
                .filter(phase -> phase != null && phase.healthThreshold() < currentHealthPercent && phase.hasHealthFloor())
                .mapToDouble(PhaseDefinition::healthFloorPercent)
                .max()
                .orElse(0.0D);
    }

    public boolean canSpawnInBiome(String biomeId) {
        if (biomeId == null || biomeId.isBlank()) {
            return true;
        }
        List<String> biomes = spawnRulesOrDefault().biomesOrDefault();
        return biomes.isEmpty() || biomes.stream().anyMatch(biomeId::equalsIgnoreCase);
    }

    public boolean canSpawnInStructure(String structureId) {
        if (structureId == null || structureId.isBlank()) {
            return !hasSpawnStructure();
        }
        List<String> structures = spawnRulesOrDefault().structuresOrDefault();
        if (!structures.isEmpty()) {
            return structures.stream().anyMatch(structureId::equalsIgnoreCase);
        }
        return spawnStructure == null || spawnStructure.isBlank() || structureId.equalsIgnoreCase(spawnStructure);
    }

    private boolean matchesUnqualifiedId(String requestedId, String actualId) {
        if (requestedId == null || actualId == null) {
            return false;
        }
        int requestedColon = requestedId.indexOf(':');
        int actualColon = actualId.indexOf(':');
        if (requestedColon >= 0 || actualColon < 0) {
            return false;
        }
        return Objects.equals(requestedId, actualId.substring(actualColon + 1));
    }
}
