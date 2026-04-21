# Getting Started

## Requirements

- NeoForge 1.21.1
- Java 21
- GeckoLib
- KubeJS is optional

## What Vitae Does

Vitae loads entity definitions from datapack JSON and uses them to drive:
- models
- animations
- attributes
- abilities
- boss bars
- death behavior
- reset behavior
- optional scripting hooks

## Basic Workflow

1. Create an entity JSON under `data/<namespace>/vitae/entities/`.
2. Create any referenced ability JSON under `data/<namespace>/vitae/abilities/`.
3. Add model and animation assets to your resource pack.
4. Reload the world or datapack.
5. Spawn the entity using command, spawn egg, or a configured natural spawn rule.

## Minimal Entity Example

```json
{
  "model": "mypack:geo/my_entity.geo.json",
  "animations": "mypack:animations/my_entity.animation.json",
  "attributes": {
    "max_health": 40,
    "movement_speed": 0.25,
    "attack_damage": 4
  }
}
```

## Notes

- If a field is omitted, Vitae falls back to sane defaults where possible.
- The demo entity `angry_boy` exists only as a showcase.
