package com.vitae.entity;

import com.vitae.animation.VitaeAnimationController;
import com.vitae.data.EntityDefinition;
import com.vitae.data.PhaseDefinition;
import com.vitae.phase.PhaseLockController;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Base class for all Vitae-managed entities.
 *
 * <p>Behaviour is driven entirely by an {@link EntityDefinition} loaded from a datapack JSON.
 * Subclass or instantiate dynamically via the Vitae registry — do not hardcode entities here.
 */
public class VitaeMob extends PathfinderMob implements GeoEntity {

    private final EntityDefinition definition;
    private final AnimatableInstanceCache animatableCache = GeckoLibUtil.createInstanceCache(this);
    private final PhaseLockController phaseLockController = new PhaseLockController();
    private int introAnimationTicksRemaining;

    protected VitaeMob(EntityType<? extends VitaeMob> type, Level level, EntityDefinition definition) {
        super(type, level);
        this.definition = definition;
    }

    public EntityDefinition getDefinition() {
        return definition;
    }

    public boolean hasIntroAnimationPending() {
        return introAnimationTicksRemaining > 0 && definition.hasIntro();
    }

    protected void playIntroAnimation() {
        introAnimationTicksRemaining = 120;
    }

    protected void clearIntroAnimation() {
        introAnimationTicksRemaining = 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (introAnimationTicksRemaining > 0) {
            introAnimationTicksRemaining--;
        }
        phaseLockController.tick(this, definition);
    }

    /** Returns the currently active phase based on the entity's health percentage. */
    public PhaseDefinition getCurrentPhase() {
        float healthPercent = this.getHealth() / this.getMaxHealth();
        return definition.getPhaseForHealth(healthPercent);
    }

    public boolean isPhaseLocked() {
        return phaseLockController.isLocked();
    }

    public boolean isSummonLockActive() {
        return phaseLockController.isSummonLockActive();
    }

    public double currentSummonLockFloorOrDefault() {
        double healthPercent = this.getHealth() / this.getMaxHealth();
        return definition.nextPhaseHealthFloorOrDefault(healthPercent);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(VitaeAnimationController.create(this, definition));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableCache;
    }
}
