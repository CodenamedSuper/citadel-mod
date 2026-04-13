package com.citadel.entity.pebblet.ai.behavior;

import com.citadel.entity.pebblet.Pebblet;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import java.util.Optional;

public class MoveTowardsAttackTargetIfOutsideRollDistance extends Behavior<Pebblet> {
    public static final int MIN_ROLL_DISTANCE = 12;

    public MoveTowardsAttackTargetIfOutsideRollDistance() {
        super(ImmutableMap.of(
                MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT
        ));
    }

    @Override
    protected void start(ServerLevel level, Pebblet entity, long gameTime) {
        Brain<Pebblet> brain = entity.getBrain();

        Optional<LivingEntity> attackTargetMemory = brain.getMemory(MemoryModuleType.ATTACK_TARGET);
        if (attackTargetMemory.isEmpty()) return;

        LivingEntity target = attackTargetMemory.get();

        BehaviorUtils.setWalkAndLookTargetMemories(entity, target, 2.0f, MIN_ROLL_DISTANCE);
    }
}
