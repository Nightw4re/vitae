package com.vitae.animation;

import com.vitae.data.EntityDefinition;
import com.vitae.data.PhaseDefinition;
import com.vitae.entity.VitaeMob;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

/**
 * GeckoLib animation controller for Vitae entities.
 *
 * <p>Plays the animation matching the current boss phase. Phase animations are expected
 * to be named after the phase ID (e.g. a phase with id {@code "phase_2"} plays animation
 * {@code "animation.entity.phase_2"}).
 *
 * <p>Falls back to {@code "animation.entity.idle"} when no phase is active.
 */
public final class VitaeAnimationController {

    private static final String IDLE = "animation.entity.idle";
    private static final String INTRO = "animation.entity.intro";
    private static final String OUTRO = "animation.entity.outro";
    private static final String CONTROLLER_NAME = "vitae_controller";

    private VitaeAnimationController() {}

    public static AnimationController<VitaeMob> create(VitaeMob entity, EntityDefinition definition) {
        return new AnimationController<>(entity, CONTROLLER_NAME, 5, state -> handleAnimation(state, entity));
    }

    /** Creates a simple idle-only controller for NPC entities. */
    public static <T extends software.bernie.geckolib.animatable.GeoAnimatable> AnimationController<T> createIdle(T entity) {
        return new AnimationController<>(entity, CONTROLLER_NAME, 5, state -> {
            state.getController().setAnimation(RawAnimation.begin().thenLoop(IDLE));
            return PlayState.CONTINUE;
        });
    }

    private static PlayState handleAnimation(AnimationState<VitaeMob> state, VitaeMob entity) {
        if (entity instanceof net.minecraft.world.entity.LivingEntity livingEntity && livingEntity.deathTime > 0) {
            state.getController().setAnimation(RawAnimation.begin().thenPlay(OUTRO));
            return PlayState.CONTINUE;
        }

        if (entity.hasIntroAnimationPending()) {
            state.getController().setAnimation(RawAnimation.begin().thenPlay(entity.getDefinition().introAnimation()));
            return PlayState.CONTINUE;
        }

        PhaseDefinition phase = entity.getCurrentPhase();
        String animName = phase != null
                ? "animation.entity." + phase.id()
                : IDLE;

        state.getController().setAnimation(RawAnimation.begin().thenLoop(animName));
        return PlayState.CONTINUE;
    }
}
