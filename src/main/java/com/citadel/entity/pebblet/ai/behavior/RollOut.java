package com.citadel.entity.pebblet.ai.behavior;

import com.citadel.Citadel;
import com.citadel.entity.pebblet.Pebblet;
import com.citadel.entity.pebblet.PebbletState;
import com.citadel.registry.CitadelMemoryModuleTypes;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class RollOut extends Behavior<Pebblet> {
    private static final int DURATION = 10;

    public RollOut() {
        super(ImmutableMap.of(
                CitadelMemoryModuleTypes.ROLL_TARGET.get(), MemoryStatus.VALUE_ABSENT
        ), DURATION);
    }

    @Override
    protected void start(ServerLevel level, Pebblet entity, long gameTime) {
        Citadel.LOGGER.info("Rolling out!");

        entity.setState(PebbletState.ROLL_OUT);
    }

    @Override
    protected void stop(ServerLevel level, Pebblet entity, long gameTime) {
        var brain = entity.getBrain();

        brain.setMemoryWithExpiry(CitadelMemoryModuleTypes.ROLL_COOLDOWN.get(), Unit.INSTANCE, 20L);

        entity.setState(PebbletState.IDLE);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Pebblet entity) {
        return entity.onGround() && entity.getState() == PebbletState.ROLL;
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Pebblet entity, long gameTime) {
        return true;
    }
}
