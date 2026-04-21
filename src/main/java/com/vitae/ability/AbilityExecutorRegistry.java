package com.vitae.ability;

import com.vitae.ability.executor.AoeExecutor;
import com.vitae.ability.executor.DashExecutor;
import com.vitae.ability.executor.CarryTargetExecutor;
import com.vitae.ability.executor.MeleeExecutor;
import com.vitae.ability.executor.PlaceBlockExecutor;
import com.vitae.ability.executor.RangedProjectileExecutor;
import com.vitae.ability.executor.SpawnEntityExecutor;
import com.vitae.ability.executor.SpinSlashExecutor;
import com.vitae.ability.executor.SummonExecutor;
import com.vitae.ability.executor.LevitateTargetExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry mapping ability type strings to their {@link AbilityExecutor} implementations.
 *
 * <p>Built-in types are registered on construction. KubeJS can register additional
 * executors at startup via {@link #register(String, AbilityExecutor)}.
 */
public final class AbilityExecutorRegistry {

    private static final AbilityExecutorRegistry INSTANCE = new AbilityExecutorRegistry();

    private final Map<String, AbilityExecutor> executors = new HashMap<>();

    private AbilityExecutorRegistry() {
        register("melee_attack",        new MeleeExecutor());
        register("ranged_projectile",   new RangedProjectileExecutor());
        register("summon",              new SummonExecutor());
        register("aoe",                 new AoeExecutor());
        register("dash",                new DashExecutor());
        register("spin_slash",          new SpinSlashExecutor());
        register("spawn_entity",        new SpawnEntityExecutor());
        register("place_block",         new PlaceBlockExecutor());
        register("levitate_target",     new LevitateTargetExecutor());
        register("carry_target",        new CarryTargetExecutor());
        registerOptional("external_spell", "com.vitae.ability.executor.ExternalSpellExecutor");
    }

    public static AbilityExecutorRegistry get() {
        return INSTANCE;
    }

    /** Registers a custom ability executor. Overwrites any existing executor for the same type. */
    public void register(String type, AbilityExecutor executor) {
        executors.put(type, executor);
    }

    private void registerOptional(String type, String className) {
        try {
            Class<?> raw = Class.forName(className);
            Object instance = raw.getDeclaredConstructor().newInstance();
            if (instance instanceof AbilityExecutor executor) {
                register(type, executor);
            }
        } catch (ReflectiveOperationException ignored) {
            // Optional executor not present in this runtime.
        }
    }

    /**
     * Returns the executor for the given ability type, or null if unknown.
     */
    public AbilityExecutor getExecutor(String type) {
        return executors.get(type);
    }
}
