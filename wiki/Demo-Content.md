# Demo Content

Vitae ships with a built-in showcase entity:

- id: `vitae:angry_boy`
- spawn command: `/vitae spawn_angry_boy`
- spawn egg: `angry_boy_spawn_egg`
- one-time spawner: `angry_boy_one_time_spawner`

## Purpose

The demo entity exists to validate:
- boss bar support
- ability scheduling
- custom model and animation integration
- optional death loot
- grab and carry spell behavior

## Current Behavior

The showcase boss uses a data-driven ability setup with:
- `vex_grab`
- `spin_slash`
- normal melee fallback

The demo is not the framework's final content model.
It is only a living integration test and example.
