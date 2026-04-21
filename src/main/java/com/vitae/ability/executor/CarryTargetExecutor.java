package com.vitae.ability.executor;

import com.vitae.ability.AbilityExecutor;
import com.vitae.data.AbilityDefinition;
import com.vitae.data.GrabTimeline;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CarryTargetExecutor implements AbilityExecutor {
    private static final Map<UUID, State> STATES = new ConcurrentHashMap<>();

    @Override
    public void execute(Mob caster, LivingEntity target, AbilityDefinition ability) {
        if (caster.level().isClientSide() || target == null) {
            return;
        }
        int duration = ability.parameters() != null ? Math.max(0, ability.parameters().durationTicks()) : 0;
        double heightOffset = ability.parameters() != null ? Math.max(0.0D, ability.parameters().heightOffset()) : 6.0D;
        UUID vexId = SpawnEntityExecutor.getLastSpawned(caster);
        if (vexId == null || !(caster.level() instanceof ServerLevel level)) {
            return;
        }
        State state = STATES.computeIfAbsent(caster.getUUID(), id -> new State(duration, heightOffset, vexId, target.getUUID()));
        state.duration = Math.max(state.duration, duration);
        state.heightOffset = heightOffset;
        state.vexId = vexId;
        state.targetId = target.getUUID();
        state.initialY = target.getY();
        state.level = level;
    }

    public static void tick(Mob caster) {
        State state = STATES.get(caster.getUUID());
        if (state == null || !(caster.level() instanceof ServerLevel level)) {
            return;
        }
        state.duration = Math.max(1, state.duration);
        LivingEntity target = level.getEntity(state.targetId) instanceof LivingEntity living ? living : null;
        LivingEntity vex = level.getEntity(state.vexId) instanceof LivingEntity living ? living : null;
        if (target == null || vex == null || !target.isAlive() || !vex.isAlive()) {
            cleanup(caster, level, state);
            return;
        }
        target.setNoGravity(true);
        vex.setNoGravity(true);
        applyScaleIfAvailable(vex, 2.0F);
        if (!state.locked) {
            state.lockedX = target.getX();
            state.lockedZ = target.getZ();
            state.locked = true;
        }
        state.ticks++;
        double targetY = GrabTimeline.targetY(state.initialY, state.heightOffset, state.ticks, state.duration);
        vex.setPos(state.lockedX, targetY + 2.5D, state.lockedZ);

        if (!state.released) {
            double currentY = target.getY();
            double nextY = Math.max(currentY, targetY);
            target.setPos(state.lockedX, nextY, state.lockedZ);
            target.setDeltaMovement(0.0D, Math.max(0.0D, targetY - currentY), 0.0D);
            target.hurtMarked = true;
            target.fallDistance = 0.0F;
        } else {
            target.setNoGravity(false);
            vex.setNoGravity(false);
            target.setDeltaMovement(0.0D, -0.55D, 0.0D);
            target.hurtMarked = true;
        }

        if (state.dripstonePos == null) {
            BlockPos pos = findDripstonePos(level, state.lockedX, targetY, state.lockedZ, caster.blockPosition());
            if (level.getBlockState(pos).isAir()) {
                level.setBlockAndUpdate(pos, Blocks.POINTED_DRIPSTONE.defaultBlockState());
                state.dripstonePos = pos;
            }
        }

        if (GrabTimeline.shouldRelease(state.ticks, state.duration)) {
            state.released = true;
        }

        if (state.released) {
            if (target.onGround() || target.isPassenger() || !target.isAlive()) {
                cleanup(caster, level, state);
            }
        }
    }

    public static boolean hasActiveGrab(Mob caster) {
        return STATES.containsKey(caster.getUUID());
    }

    public static void forceReset(Mob caster) {
        State state = STATES.get(caster.getUUID());
        if (state == null || !(caster.level() instanceof ServerLevel level)) {
            return;
        }
        cleanup(caster, level, state);
    }

    private static void cleanup(Mob caster, ServerLevel level, State state) {
        LivingEntity vex = level.getEntity(state.vexId) instanceof LivingEntity living ? living : null;
        if (vex != null && vex.isAlive()) {
            vex.setNoGravity(false);
            vex.discard();
        }
        LivingEntity target = level.getEntity(state.targetId) instanceof LivingEntity living ? living : null;
        if (target != null) {
            target.setNoGravity(false);
            target.setDeltaMovement(0.0D, -0.75D, 0.0D);
            target.fallDistance = 0.0F;
            target.hurtMarked = true;
        }
        if (state.dripstonePos != null && level.getBlockState(state.dripstonePos).is(Blocks.POINTED_DRIPSTONE)) {
            level.destroyBlock(state.dripstonePos, false);
        }
        STATES.remove(caster.getUUID());
    }

    private static BlockPos findDripstonePos(ServerLevel level, double x, double targetY, double z, BlockPos casterPos) {
        BlockPos base = BlockPos.containing(x, targetY - 1.0D, z);
        if (isSafeDripstoneSpot(level, base)) {
            return base;
        }
        for (int offset = 1; offset <= 4; offset++) {
            BlockPos down = base.below(offset);
            if (isSafeDripstoneSpot(level, down)) {
                return down;
            }
        }
        return casterPos.below();
    }

    private static boolean isSafeDripstoneSpot(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).isAir() && !level.getBlockState(pos.below()).isAir();
    }

    private static void applyScaleIfAvailable(LivingEntity entity, float scale) {
        try {
            Method method = entity.getClass().getMethod("setScale", float.class);
            method.invoke(entity, scale);
        } catch (ReflectiveOperationException ignored) {
            // Scale is optional. Continue without it if the runtime does not expose it.
        }
    }

    private static final class State {
        private int duration;
        private double heightOffset;
        private UUID vexId;
        private UUID targetId;
        private int ticks;
        private boolean released;
        private double initialY;
        private ServerLevel level;
        private BlockPos dripstonePos;
        private boolean locked;
        private double lockedX;
        private double lockedZ;

        private State(int duration, double heightOffset, UUID vexId, UUID targetId) {
            this.duration = duration;
            this.heightOffset = heightOffset;
            this.vexId = vexId;
            this.targetId = targetId;
        }
    }
}
