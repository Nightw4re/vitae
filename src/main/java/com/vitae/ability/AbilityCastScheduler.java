package com.vitae.ability;

import com.vitae.data.AbilityDefinition;
import com.vitae.data.AbilityReference;
import com.vitae.data.AbilityStepDefinition;
import com.vitae.data.AbilityModifierParser;
import com.vitae.data.EntityDefinition;
import com.vitae.registry.VitaeRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/**
 * Tick-driven scheduler for JSON-defined ability casts.
 *
 * <p>Supports single-step abilities directly and {@code sequence} abilities composed from
 * child steps. One scheduler instance is meant to be attached to one caster.
 */
public final class AbilityCastScheduler {

    private final AbilityCooldownTracker cooldowns = new AbilityCooldownTracker();
    private final Deque<RunningCast> activeCasts = new ArrayDeque<>();
    private boolean combatPaused;

    public void tick(Mob caster, EntityDefinition definition) {
        if (caster.level().isClientSide()) {
            return;
        }

        cooldowns.tick();
        tickActiveCast(caster);
        if (isCasting()) {
            return;
        }

        LivingEntity target = caster.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }

        double distance = caster.distanceTo(target);
        double healthPercent = getHealthPercent(caster);
        List<AbilityDefinition> candidates = resolveAbilities(definition);
        if (candidates.isEmpty()) {
            return;
        }
        AbilityDefinition selected = AbilitySelector.select(candidates, cooldowns, distance, healthPercent, new Random(caster.getRandom().nextLong()));
        if (selected == null) {
            return;
        }

        cooldowns.startCooldown(selected.id(), Math.max(1, selected.cooldownTicks()));
        if (isSequence(selected)) {
            activeCasts.addLast(new RunningCast(selected, randomDelayTicks(caster, selected)));
            pauseCombat(caster);
            return;
        }

        executeAbility(caster, selected);
    }

    public void stopAll(Mob caster) {
        activeCasts.clear();
        combatPaused = false;
        cooldowns.resetAll();
        restoreCombatState(caster);
    }

    public boolean isCasting() {
        return !activeCasts.isEmpty();
    }

    private void tickActiveCast(Mob caster) {
        if (activeCasts.isEmpty()) {
            return;
        }

        RunningCast cast = activeCasts.peekFirst();
        if (cast.remainingDelay > 0) {
            cast.remainingDelay--;
            return;
        }

        if (!cast.started) {
            cast.started = true;
            executeNextStep(caster, cast);
            return;
        }

        if (cast.stepDelayRemaining > 0) {
            cast.stepDelayRemaining--;
            return;
        }

        executeNextStep(caster, cast);
    }

    private void executeNextStep(Mob caster, RunningCast cast) {
        List<AbilityStepDefinition> steps = cast.ability.steps();
        if (steps == null || cast.stepIndex >= steps.size()) {
            activeCasts.removeFirst();
            restoreCombatState(caster);
            return;
        }

        AbilityStepDefinition step = steps.get(cast.stepIndex++);
        AbilityDefinition stepAbility = new AbilityDefinition(
                cast.ability.id() + "/" + step.ability(),
                step.ability(),
                0,
                null,
                step.parameters() != null ? step.parameters() : cast.ability.parameters(),
                List.of(),
                0,
                0,
                true
        );
        executeAbility(caster, stepAbility);
        cast.stepDelayRemaining = Math.max(0, step.delayTicks());
        if (cast.stepIndex >= steps.size()) {
            activeCasts.removeFirst();
            restoreCombatState(caster);
        }
    }

    private void executeAbility(Mob caster, AbilityDefinition ability) {
        AbilityExecutor executor = AbilityExecutorRegistry.get().getExecutor(ability.type());
        if (executor == null) {
            return;
        }
        executor.execute(caster, caster.getTarget(), ability);
    }

    private List<AbilityDefinition> resolveAbilities(EntityDefinition definition) {
        if (definition == null || definition.abilities() == null || definition.abilities().isEmpty()) {
            return List.of();
        }

        List<AbilityDefinition> result = new ArrayList<>(definition.abilities().size());
        for (AbilityReference reference : definition.abilities()) {
            if (reference == null || reference.id() == null) {
                continue;
            }
            AbilityDefinition ability = resolveAbility(definition, reference.id());
            if (ability != null) {
                result.add(applyReferenceModifiers(ability, reference));
            }
        }
        return result;
    }

    private AbilityDefinition resolveAbility(EntityDefinition definition, String abilityId) {
        if (abilityId.indexOf(':') < 0) {
            AbilityDefinition namespaced = VitaeRegistry.get().getAbility(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("vitae", abilityId));
            if (namespaced != null) {
                return namespaced;
            }
        }
        return VitaeRegistry.get().getAbility(net.minecraft.resources.ResourceLocation.parse(abilityId));
    }

    private double getHealthPercent(Mob caster) {
        float maxHealth = caster.getMaxHealth();
        if (maxHealth <= 0.0F) {
            return 0.0D;
        }
        return caster.getHealth() / maxHealth;
    }

    private boolean isSequence(AbilityDefinition ability) {
        return "sequence".equalsIgnoreCase(ability.type()) || (ability.steps() != null && !ability.steps().isEmpty());
    }

    private int randomDelayTicks(Mob caster, AbilityDefinition ability) {
        int min = Math.max(0, ability.randomDelayTicksMin());
        int max = Math.max(min, ability.randomDelayTicksMax());
        if (max <= min) {
            return min;
        }
        return caster.getRandom().nextInt(max - min + 1) + min;
    }

    private AbilityDefinition applyReferenceModifiers(AbilityDefinition base, AbilityReference reference) {
        if (base == null || reference == null) {
            return base;
        }
        int cooldown = base.cooldownTicks();
        String modifier = reference.cooldownTicks();
        if (modifier != null && !modifier.isBlank()) {
            cooldown = AbilityModifierParser.parseCooldownTicks(modifier, cooldown);
        }
        return new AbilityDefinition(
                base.id(),
                base.type(),
                cooldown,
                base.condition(),
                base.parameters(),
                base.steps(),
                base.randomDelayTicksMin(),
                base.randomDelayTicksMax(),
                base.interruptible()
        );
    }

    private void pauseCombat(Mob caster) {
        combatPaused = true;
        caster.getNavigation().stop();
        caster.setAggressive(false);
    }

    private void restoreCombatState(Mob caster) {
        if (!combatPaused) {
            return;
        }
        combatPaused = false;
        LivingEntity target = caster.getTarget();
        caster.setAggressive(target != null && target.isAlive());
    }

    private static final class RunningCast {
        private final AbilityDefinition ability;
        private int remainingDelay;
        private int stepIndex;
        private int stepDelayRemaining;
        private boolean started;

        private RunningCast(AbilityDefinition ability, int remainingDelay) {
            this.ability = ability;
            this.remainingDelay = Math.max(0, remainingDelay);
        }
    }
}
