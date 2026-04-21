# Manual Testing Plan - Vitae

> Launch the game with `npm run run:client`, make sure you are an operator (`/op <name>`), and enable cheats in the world.

---

## Prerequisites

Before testing, prepare demo datapacks with test entities. Place them in:

```text
runs/client/saves/<world>/datapacks/vitae_test/data/test/vitae/entities/
runs/client/saves/<world>/datapacks/vitae_test/data/test/vitae/npcs/
```

The repository also includes a built-in demo entity, `vitae:angry_boy`.
All three demo spawn methods are available at the same time: command, spawn egg, and one-time spawner block.

---

## 0. Built-in demo entity

Use any of the following:

**Command mode**

```text
/vitae spawn_angry_boy
```

**Spawn egg mode**

```text
/give @s vitae:angry_boy_spawn_egg
```

**One-time spawner block**

```text
/give @s vitae:angry_boy_one_time_spawner
```

**Verify:**

- [ ] The command spawns `vitae:angry_boy`
- [ ] The spawn egg spawns `vitae:angry_boy`
- [ ] The one-time spawner block spawns `vitae:angry_boy` and destroys itself
- [ ] Right-clicking the one-time spawner with an empty hand cycles its activation distance
- [ ] The entity renders as a large grass block and is visibly taller than the player silhouette
- [ ] The entity attacks nearby players
- [ ] The entity shows a boss bar when tracked by a player
- [ ] The entity uses the built-in definition values for health, speed, damage, and armor

---

## 1. Passive NPC (idle)

**File:** `data/test/vitae/npcs/villager_idle.json`

```json
{
  "model": "vitae:geo/default.geo.json",
  "animations": "vitae:animations/default.animation.json",
  "behavior": { "type": "idle" }
}
```

**In game:**

```text
/summon test:villager_idle
```

**Verify:**

- [ ] The NPC spawns in place
- [ ] It does not stand completely frozen and has an idle animation
- [ ] It does not react to the player, move toward them, or attack
- [ ] It does not disappear after a short time and remains persistent

---

## 2. Passive NPC (follow)

**File:** `data/test/vitae/npcs/companion.json`

```json
{
  "model": "vitae:geo/default.geo.json",
  "animations": "vitae:animations/default.animation.json",
  "behavior": { "type": "follow", "followRange": 16.0 }
}
```

```text
/summon test:companion
```

**Verify:**

- [ ] The NPC follows you within a range of 16 blocks
- [ ] It stops following when you move farther away
- [ ] It does not attack you or other mobs

---

## 3. Passive NPC (guard)

**File:** `data/test/vitae/npcs/guard.json`

```json
{
  "model": "vitae:geo/default.geo.json",
  "animations": "vitae:animations/default.animation.json",
  "behavior": { "type": "guard", "guardRadius": 8.0 }
}
```

```text
/summon test:guard
```

**Verify:**

- [ ] The NPC patrols within roughly 8 blocks of its spawn point
- [ ] It returns to its original area if pushed too far away
- [ ] It does not attack

---

## 4. Hostile mob (single phase)

**File:** `data/test/vitae/entities/grunt.json`

```json
{
  "model": "vitae:geo/default.geo.json",
  "animations": "vitae:animations/default.animation.json",
  "attributes": {
    "max_health": 30,
    "movement_speed": 0.3,
    "attack_damage": 5
  },
  "phases": [
    {
      "id": "phase_1",
      "health_threshold": 1.0,
      "abilities": ["melee_attack"]
    }
  ]
}
```

```text
/summon test:grunt
```

**Verify:**

- [ ] The mob attacks the player when approached
- [ ] It has the correct health value of 30
- [ ] It can be killed normally
- [ ] On death it despawns, or plays a death animation if one is configured

---

## 5. Boss (two phases)

**File:** `data/test/vitae/entities/test_boss.json`

```json
{
  "model": "vitae:geo/default.geo.json",
  "animations": "vitae:animations/default.animation.json",
  "attributes": {
    "max_health": 200,
    "movement_speed": 0.25,
    "attack_damage": 15
  },
  "intro_animation": "intro",
  "phases": [
    {
      "id": "phase_1",
      "health_threshold": 1.0,
      "abilities": ["melee_attack"],
      "animation": "walk"
    },
    {
      "id": "phase_2",
      "health_threshold": 0.5,
      "abilities": ["melee_attack", "dash"],
      "animation": "walk",
      "transition": {
        "animation": "roar",
        "invulnerable": true,
        "duration_ticks": 60
      }
    }
  ],
  "boss_bar": { "color": "RED", "overlay": "PROGRESS" },
  "on_reset": {
    "full_heal": true,
    "return_to_spawn": true,
    "clear_phases": true
  },
  "on_death": {
    "animation": "death",
    "delay_loot": true
  }
}
```

```text
/summon test:test_boss
```

**Verify:**

- [ ] The boss bar appears when the boss aggros
- [ ] The intro animation plays and the boss is invulnerable during it
- [ ] Phase 1 is active at full health
- [ ] At 50% health, the transition starts and the boss cannot be damaged for 3 seconds
- [ ] Phase 2 becomes active after the transition
- [ ] The boss resets when the player leaves: it returns, fully heals, and goes back to phase 1
- [ ] The death animation plays and loot drops only afterward

**Shortcut for testing phase 2:**

```text
/data merge entity @e[type=test:test_boss,limit=1] {Health:99f}
```

---

## 6. Mob with a summon ability

**Add this to phase 1 of the boss:**

```json
"abilities": ["melee_attack", "summon_minions"]
```

**Ability JSON** (`data/test/vitae/abilities/summon_minions.json`):

```json
{
  "id": "summon_minions",
  "type": "summon",
  "cooldown_ticks": 200,
  "parameters": { "entity": "test:grunt", "count": 3 }
}
```

**Verify:**

- [ ] The boss spawns 3 grunts when the ability is used
- [ ] The cooldown works and the ability is not spammed

---

## 7. Escape and reset test

```text
/summon test:test_boss
```

1. Aggro the boss
2. Bring it below 50% health so phase 2 is active
3. Run far away, or use `/kill @s` and respawn

**Verify:**

- [ ] The boss returns to its spawn position
- [ ] It fully heals
- [ ] The phase resets to phase 1
- [ ] The boss bar disappears

---

## 8. KubeJS event test

In `runs/client/kubejs/server_scripts/vitae_test.js`:

```js
VitaeEvents.onPhaseChange(event => {
    console.log(`Phase changed to: ${event.newPhase}`);
});

VitaeEvents.onDeath(event => {
    console.log(`Boss died: ${event.entity}`);
});
```

Watch the console while testing the boss.

---

## Cleanup commands

```text
/kill @e[type=!player]
/reload
```
