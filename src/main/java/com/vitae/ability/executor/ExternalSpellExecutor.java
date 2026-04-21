package com.vitae.ability.executor;

import com.vitae.ability.AbilityExecutor;
import com.vitae.ability.ExternalSpellBridge;
import com.vitae.data.AbilityDefinition;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.List;
import java.util.ServiceLoader;

public final class ExternalSpellExecutor implements AbilityExecutor {

    private static final List<ExternalSpellBridge> BRIDGES = ServiceLoader.load(ExternalSpellBridge.class)
            .stream()
            .map(ServiceLoader.Provider::get)
            .toList();

    @Override
    public void execute(Mob caster, LivingEntity target, AbilityDefinition ability) {
        String providerId = ability.parameters() != null ? ability.parameters().providerId() : null;
        if (providerId == null || providerId.isBlank()) {
            return;
        }
        for (ExternalSpellBridge bridge : BRIDGES) {
            if (providerId.equalsIgnoreCase(bridge.providerId())) {
                bridge.cast(caster, target, ability);
                return;
            }
        }
    }
}
