# Vitae

A data-driven entity framework for NeoForge 1.21.1. Define custom animated mobs, bosses, and NPCs entirely through datapacks and KubeJS — no additional mod required.

## Features

- **GeckoLib-powered animations** — full keyframe animation support via Blockbench
- **Data-driven** — define entities, phases, abilities and loot in JSON datapacks
- **KubeJS API** — script custom behavior, conditions and events
- **Multi-phase bosses** — health thresholds trigger phase transitions with new AI, model changes and animations
- **Universal** — works for bosses, friendly NPCs, hostile mobs and anything in between

## Requirements

- NeoForge 1.21.1
- GeckoLib 4.8+
- KubeJS 2101+ *(optional, for scripting)*

## Usage

Entities are defined in your datapack under `data/<namespace>/vitae/entities/`.

```json
{
  "model": "mypack:geo/angry_boy.geo.json",
  "animations": "mypack:animations/angry_boy.animation.json",
  "attributes": {
    "max_health": 200,
    "movement_speed": 0.3,
    "attack_damage": 12
  },
  "intro_animation": "intro",
  "phases": [
    {
      "id": "phase_1",
      "health_threshold": 1.0,
      "abilities": ["melee_attack", "summon_reinforcements"],
      "animation": "phase_1_idle"
    },
    {
      "id": "phase_2",
      "health_threshold": 0.5,
      "model": "mypack:geo/angry_boy_enraged.geo.json",
      "scale": 1.4,
      "transition": {
        "animation": "phase_2_transition",
        "invulnerable": true
      },
      "abilities": ["staff_beam", "melee_attack"],
      "animation": "phase_2_idle"
    }
  ],
  "on_death": {
    "animation": "death_cinematic",
    "delay_loot": true
  },
  "on_reset": {
    "full_heal": true,
    "return_to_spawn": true,
    "animation": "reset_idle"
  },
  "loot_table": "mypack:entities/angry_boy"
}
```

## Built-in Test Entity

The mod includes a built-in demo boss registered as `vitae:angry_boy`. It is intended for local testing and validation of the spawn pipeline.
By default it renders as an oversized `grass_block`, so you can verify spawning immediately without creating custom assets first.

All three demo spawn methods are always available:

- `/vitae spawn_angry_boy`
- `vitae:angry_boy_spawn_egg`
- `vitae:angry_boy_one_time_spawner`

The one-time spawner is a block that looks like a vanilla spawner. Place it in the world, then right-click it with an empty hand to cycle its activation distance between preset values. When a player enters range, it spawns one `angry_boy` and destroys itself.

External spell mods can be integrated through the optional `external_spell` ability type:
```json
{
  "id": "spell_burst",
  "type": "external_spell",
  "cooldown_ticks": 200,
  "parameters": {
    "provider_id": "ars_nouveau",
    "spell_id": "ars_nouveau:amplify_burst"
  }
}
```
Vitae will only execute it if a matching provider bridge is present at runtime.

## Boss System

### Phases

Each phase activates when the boss's health drops to or below its `health_threshold`. Phases can change:

- **Model** — swap to a different GeckoLib `.geo.json` at runtime
- **Scale** — resize the entity (e.g. grow larger during an enraged phase)
- **Abilities** — different ability set per phase
- **Animation** — idle/attack animations specific to the phase

### Phase Transitions

When the boss enters a new phase, a transition can play before the new phase starts:

```json
"transition": {
  "animation": "phase_2_transition",
  "invulnerable": true,
  "duration_ticks": 60
}
```

- `animation` — plays once during the transition
- `invulnerable` — boss cannot take damage while the transition animation plays
- `duration_ticks` — overrides animation length if set

### Intro Animation

An optional animation that plays when the boss first aggros a player. The boss is invulnerable during the intro.

```json
"intro_animation": "intro"
```

### Death Behavior

Instead of despawning instantly, the boss can play a cinematic death animation before dropping loot:

```json
"on_death": {
  "animation": "death_cinematic",
  "delay_loot": true,
  "become_friendly": false
}
```

Setting `become_friendly: true` turns the boss into a passive NPC on defeat instead of killing it — useful for quest-driven encounters.

### Reset Behavior

If the boss loses aggro (all players are dead or flee out of range), it can reset instead of staying in a damaged state:

```json
"on_reset": {
  "full_heal": true,
  "return_to_spawn": true,
  "animation": "reset_idle",
  "clear_phases": true
}
```

- `full_heal` — restores the boss to full health
- `return_to_spawn` — teleports back to its spawn position
- `animation` — plays while returning/healing
- `clear_phases` — resets to phase 1

## Development

```bash
npm test          # run unit tests
npm run coverage  # run with JaCoCo coverage (80% required)
npm run run:client  # launch Minecraft client
npm run run:server  # launch Minecraft server
```

If you find a bug or want to request a feature, please open an issue at:
https://github.com/Nightw4re/vitae/issues

## License

MIT
