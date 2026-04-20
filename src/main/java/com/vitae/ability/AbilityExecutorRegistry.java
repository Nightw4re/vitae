package com.vitae.ability;

import com.vitae.ability.executor.AoeExecutor;
import com.vitae.ability.executor.DashExecutor;
import com.vitae.ability.executor.MeleeExecutor;
import com.vitae.ability.executor.RangedProjectileExecutor;
import com.vitae.ability.executor.SummonExecutor;

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
    }

    public static AbilityExecutorRegistry get() {
        return INSTANCE;
    }

    /** Registers a custom ability executor. Overwrites any existing executor for the same type. */
    public void register(String type, AbilityExecutor executor) {
        executors.put(type, executor);
    }

    /**
     * Returns the executor for the given ability type, or null if unknown.
     */
    public AbilityExecutor getExecutor(String type) {
        return executors.get(type);
    }
}
