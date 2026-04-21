# Ability System

Vitae abilities are data-driven building blocks that can be composed in entity JSON.

## Ability JSON

Ability definitions live under:

```text
data/<namespace>/vitae/abilities/<id>.json
```

## Built-in Executors

Current built-in executors include:
- `melee_attack`
- `ranged_projectile`
- `summon`
- `aoe`
- `dash`
- `spin_slash`
- `spawn_entity`
- `place_block`
- `levitate_target`
- `carry_target`
- `external_spell`

## References and Modifiers

Entity JSON references abilities by id and can apply local modifiers.

Example:

```json
{ "id": "vitae:vex_grab", "cooldown_ticks": "200" }
```

Supported cooldown modifiers:
- `200` absolute override
- `+120` additive modifier
- `*0.9` multiplicative modifier

## Sequence Abilities

Sequence abilities are composed from smaller steps.
Each step uses a built-in executor and can carry its own delay.

This is the preferred way to build complex boss spells without hardcoding the full flow in Java.

## External Spell Bridges

`external_spell` is optional.
It allows Vitae to call into another mod's spell system when a compatible bridge is present.

This keeps the core mod runnable even if that external mod is missing.
