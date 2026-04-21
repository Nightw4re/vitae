# Entity Definitions

Entity definitions live under:

```text
data/<namespace>/vitae/entities/<id>.json
```

## Core Fields

- `model`
- `animations`
- `attributes`
- `phases`
- `abilities`
- `loot_table`
- `xp_reward`
- `intro_animation`
- `on_death`
- `on_reset`
- `boss_bar`
- `combat`
- `equipment`
- `spawn_structure`
- `spawn_rules`

## Phases

Phases are now treated as visual states and boss behavior hints.
Ability selection is driven by ability definitions and their conditions.

## Abilities

Entity JSON should only reference abilities by id, optionally with modifiers.

Example:

```json
"abilities": [
  { "id": "vitae:vex_grab", "cooldown_ticks": "200" },
  { "id": "vitae:spin_slash" },
  { "id": "melee_attack" }
]
```

## Spawn Structure

`spawn_structure` is a legacy single-structure restriction.
Prefer `spawn_rules.structures` for new content.

## Spawn Rules

Use `spawn_rules` for natural spawn restrictions and caps.
Bosses ignore natural spawn rules.

See [Spawn Rules](Spawn-Rules).
