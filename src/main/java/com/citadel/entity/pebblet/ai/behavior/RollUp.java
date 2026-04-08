package com.citadel.entity.pebblet.ai.behavior;

import com.citadel.entity.pebblet.Pebblet;
import com.citadel.entity.pebblet.PebbletState;
import com.citadel.registry.CitadelMemoryModuleTypes;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class RollUp extends Behavior<Pebblet> {
    private static final int DURATION = 10;

    private EntityTracker targetTracker = null;

    public RollUp() {
        super(ImmutableMap.of(
                MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT
        ), DURATION);
    }

    @Override
    protected void start(ServerLevel level, Pebblet entity, long gameTime) {
        var brain = entity.getBrain();
        var attackTarget = brain.getMemory(MemoryModuleType.ATTACK_TARGET);

        if (attackTarget.isPresent()) {
            this.targetTracker = new EntityTracker(attackTarget.get(), false);

            brain.setMemory(MemoryModuleType.LOOK_TARGET, this.targetTracker);
        }

        brain.eraseMemory(MemoryModuleType.WALK_TARGET);

        entity.setState(PebbletState.ROLL_UP);
    }

    @Override
    protected void tick(ServerLevel level, Pebblet entity, long gameTime) {
        var brain = entity.getBrain();

        if (this.targetTracker != null) {
            brain.setMemory(CitadelMemoryModuleTypes.ROLL_TARGET_POS.get(), targetTracker.currentBlockPosition());
        }
    }

    @Override
    protected void stop(ServerLevel level, Pebblet entity, long gameTime) {
        var brain = entity.getBrain();

        brain.eraseMemory(MemoryModuleType.LOOK_TARGET);

        entity.setState(PebbletState.ROLL);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Pebblet entity, long gameTime) {
        return true;
    }
}
