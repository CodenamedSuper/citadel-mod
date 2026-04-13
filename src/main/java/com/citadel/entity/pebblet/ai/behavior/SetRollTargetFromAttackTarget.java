package com.citadel.entity.pebblet.ai.behavior;

import com.citadel.entity.pebblet.Pebblet;
import com.citadel.registry.CitadelMemoryModuleTypes;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
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

public class SetRollTargetFromAttackTarget extends Behavior<Pebblet> {
    public static final float MAX_ROLL_DISTANCE = 12.0f;

    public SetRollTargetFromAttackTarget() {
        super(ImmutableMap.of(
                MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT,
                CitadelMemoryModuleTypes.ROLL_TARGET.get(), MemoryStatus.VALUE_ABSENT,
                CitadelMemoryModuleTypes.ROLL_COOLDOWN.get(), MemoryStatus.VALUE_ABSENT
        ));
    }

    @Override
    protected void start(ServerLevel level, Pebblet entity, long gameTime) {
        var brain = entity.getBrain();

        Optional<LivingEntity> attackTargetMemory = brain.getMemory(MemoryModuleType.ATTACK_TARGET);
        if (attackTargetMemory.isEmpty()) return;

        LivingEntity target = attackTargetMemory.get();

        entity.getNavigation().stop();

        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(
                target,
                true
        ));
        this.tryGetRollTarget(entity, target, level);
    }

    private void tryGetRollTarget(Pebblet owner, LivingEntity target, ServerLevel level) {
        var brain = owner.getBrain();

        Optional<NearestVisibleLivingEntities> nearestVisibleLivingEntitiesMemory = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
        if (nearestVisibleLivingEntitiesMemory.isEmpty()) return;

        NearestVisibleLivingEntities visibleEntities = nearestVisibleLivingEntitiesMemory.get();

        if (visibleEntities.contains(target)) {
            this.calculateRollTargetPosition(owner, target, level).ifPresent(rollPos -> {
                brain.setMemory(CitadelMemoryModuleTypes.ROLL_TARGET.get(), rollPos);
            });
        }
    }

    private Optional<Vec3> calculateRollTargetPosition(Pebblet owner, LivingEntity target, ServerLevel level) {
        Vec3 direction = new Vec3(
                target.getX() - owner.getX(),
                0,
                target.getZ() - owner.getZ()
        ).normalize();

        BlockHitResult result = level.clip(new ClipContext(
                owner.position(),
                owner.position().add(direction.scale(MAX_ROLL_DISTANCE)),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.ANY,
                CollisionContext.empty()
        ));

        BlockPos pos = result.getBlockPos();

        if (!this.isWalkable(owner, pos)) {
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
}
