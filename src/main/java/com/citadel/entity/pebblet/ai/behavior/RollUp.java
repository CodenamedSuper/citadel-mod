package com.citadel.entity.pebblet.ai.behavior;

import com.citadel.entity.pebblet.Pebblet;
import com.citadel.entity.pebblet.PebbletState;
import com.citadel.registry.CitadelMemoryModuleTypes;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class RollUp extends Behavior<Pebblet> {
    private static final int DURATION = 10;

    public RollUp() {
        super(ImmutableMap.of(
                MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED,
                CitadelMemoryModuleTypes.ROLL_TARGET.get(), MemoryStatus.VALUE_PRESENT,
                CitadelMemoryModuleTypes.ROLL_COOLDOWN.get(), MemoryStatus.VALUE_ABSENT
        ), DURATION);
    }

    @Override
    protected void start(ServerLevel level, Pebblet entity, long gameTime) {
        entity.setState(PebbletState.ROLL_UP);
    }

    @Override
    protected void stop(ServerLevel level, Pebblet entity, long gameTime) {
        entity.setState(PebbletState.ROLL);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Pebblet entity) {
        return entity.onGround() && entity.getState() == PebbletState.IDLE;
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Pebblet entity, long gameTime) {
        return true;
    }
}
