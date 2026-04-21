# KubeJS API

Vitae exposes an optional KubeJS integration for event handling and custom ability logic.

## Entry Point

The binding is registered as:

```js
VitaeEvents
```

It is available in server-side KubeJS scripts when the optional KubeJS dependency is present.

## Available Events

- `VitaeEvents.onSpawn`
- `VitaeEvents.onDeath`
- `VitaeEvents.onPhaseChange`
- `VitaeEvents.onAbilityUsed`
- `VitaeEvents.onReset`

## Custom Ability Registration

You can register a custom ability executor from JavaScript:

```js
VitaeEvents.registerAbility('staff_beam', (caster, target, ability) => {
  console.log('Casting staff_beam for ' + caster)
})
```

This lets modpack authors add ability behavior without changing the Java code.

## Event Examples

```js
VitaeEvents.onSpawn(event => {
  console.log('Spawned entity: ' + event.entityId)
})

VitaeEvents.onDeath(event => {
  console.log('Entity died: ' + event.entityId + ', friendly=' + event.becameFriendly)
})

VitaeEvents.onPhaseChange(event => {
  console.log('Phase changed from ' + event.previous.id + ' to ' + event.next.id)
})
```

## Notes

- KubeJS is optional.
- The core mod must still load and function without it.
- Java-side ability executors remain the source of truth for built-in mechanics.
