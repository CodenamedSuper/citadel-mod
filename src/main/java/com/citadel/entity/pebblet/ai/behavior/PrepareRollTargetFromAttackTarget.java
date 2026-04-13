package com.citadel.entity.pebblet.ai.behavior;

import com.citadel.Citadel;
import com.citadel.entity.pebblet.Pebblet;
import com.citadel.registry.CitadelMemoryModuleTypes;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.Optional;

public class PrepareRollTargetFromAttackTarget extends Behavior<Pebblet> {
    public static final float ROLL_DISTANCE = 12.0f;

    public PrepareRollTargetFromAttackTarget() {
        super(ImmutableMap.of(
                MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT,
                CitadelMemoryModuleTypes.ROLL_TARGET.get(), MemoryStatus.VALUE_ABSENT,
                CitadelMemoryModuleTypes.ROLL_COOLDOWN.get(), MemoryStatus.VALUE_ABSENT
        ), 100);
    }

    @Override
    protected void tick(ServerLevel level, Pebblet owner, long gameTime) {
        var brain = owner.getBrain();

        Optional<LivingEntity> attackTargetMemory = brain.getMemory(MemoryModuleType.ATTACK_TARGET);
        if (attackTargetMemory.isEmpty()) return;

        Optional<NearestVisibleLivingEntities> nearestVisibleLivingEntitiesMemory = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
        if (nearestVisibleLivingEntitiesMemory.isEmpty()) return;

        LivingEntity target = attackTargetMemory.get();
        NearestVisibleLivingEntities visibleEntities = nearestVisibleLivingEntitiesMemory.get();

        if (visibleEntities.contains(target) && owner.getY() >= target.getY() && owner.distanceTo(target) < ROLL_DISTANCE) {
            brain.eraseMemory(MemoryModuleType.WALK_TARGET);

            this.calculateRollTargetPosition(owner, target, level).ifPresent(rollPos ->
                    brain.setMemory(CitadelMemoryModuleTypes.ROLL_TARGET.get(), rollPos)
            );
        }
        else {
            BehaviorUtils.setWalkAndLookTargetMemories(owner, target, 2.0f, 0);
        }
    }

    private Optional<Vec3> calculateRollTargetPosition(Pebblet owner, LivingEntity target, ServerLevel level) {
        Vec3 direction = new Vec3(
                target.getX() - owner.getX(),
                0,
                target.getZ() - owner.getZ()
        ).normalize();

        BlockHitResult result = level.clip(new ClipContext(
                target.position(),
                target.position().add(direction.scale(ROLL_DISTANCE / 2)),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.ANY,
                CollisionContext.empty()
        ));

        BlockPos pos = result.getBlockPos();

        if (!this.isWalkable(owner, target.blockPosition())) {
            return Optional.empty();
        }

        if (result.getType() == HitResult.Type.MISS) {
            return Optional.of(pos.getBottomCenter());
        }

        return Optional.of(pos.getBottomCenter().subtract(direction.scale(0.5f)));
    }

    private boolean isWalkable(Pebblet owner, BlockPos pos) {
        return owner.getNavigation().isStableDestination(pos) && owner.getPathfindingMalus(
                WalkNodeEvaluator.getPathTypeStatic(owner, pos)
        ) == 0;
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Pebblet entity, long gameTime) {
        return !entity.getBrain().hasMemoryValue(CitadelMemoryModuleTypes.ROLL_TARGET.get());
    }
}
