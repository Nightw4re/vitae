# Loot Definitions

Loot for Vitae entities is data-driven and loaded from the datapack reload pass.

## Location

Place entity loot tables here:

```text
data/<namespace>/loot_table/entities/<id>.json
```

For the built-in demo boss, the active table is:

```text
data/vitae/loot_table/entities/angry_boy.json
```

## Entity Link

The entity definition points to the loot table key through `loot_table`:

```json
{
  "loot_table": "vitae:entities/angry_boy"
}
```

That key is what the death/drop flow resolves at runtime.

## Table Format

Use the standard Minecraft `minecraft:entity` loot table format.

Example:

```json
{
  "type": "minecraft:entity",
  "pools": [
    {
      "rolls": 1,
      "entries": [
        { "type": "minecraft:item", "name": "minecraft:calcite", "weight": 6 },
        { "type": "minecraft:item", "name": "minecraft:andesite", "weight": 3 },
        { "type": "minecraft:item", "name": "minecraft:granite", "weight": 2 },
        { "type": "minecraft:item", "name": "minecraft:diorite", "weight": 1 }
      ],
      "functions": [
        {
          "function": "minecraft:set_count",
          "count": {
            "type": "minecraft:uniform",
            "min": 2,
            "max": 8
          }
        }
      ]
    }
  ]
}
```

## Notes

- The loot table key must match the entity definition.
- The resource path is loaded during reload, not at death time.
- If the table is missing or invalid, the entity will die without drops.

See also:
- [Entity Definitions](Entity-Definitions)
- [Demo Content](Demo-Content)
