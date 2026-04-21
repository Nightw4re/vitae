# Spawn Rules

Vitae supports optional natural spawn restrictions for non-boss entities.

## Supported Fields

```json
"spawn_rules": {
  "biomes": ["minecraft:plains", "minecraft:forest"],
  "structures": ["minecraft:ancient_city"],
  "max_nearby_global": 8,
  "max_nearby_per_biome": 3,
  "max_nearby_per_structure": 1
}
```

## Meaning

- `biomes` limits natural spawns to the listed biome ids
- `structures` limits natural spawns to the listed structure ids
- `max_nearby_global` limits total nearby entities of the same type
- `max_nearby_per_biome` limits nearby entities in the same biome
- `max_nearby_per_structure` limits nearby entities in the same structure

## Important Rules

- These rules apply only to natural spawn flows.
- Spawn eggs, commands, and other explicit spawns are not blocked by this system.
- Boss entities ignore natural spawn rules.

## Legacy Field

`spawn_structure` is still supported for compatibility.
New content should prefer `spawn_rules.structures`.
