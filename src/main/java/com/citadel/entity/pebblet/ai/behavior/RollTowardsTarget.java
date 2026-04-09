package com.citadel.entity.pebblet.ai.behavior;

import com.citadel.entity.pebblet.Pebblet;
import com.citadel.registry.CitadelMemoryModuleTypes;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.Vec3;

public class RollTowardsTarget extends Behavior<Pebblet> {

    public RollTowardsTarget() {
        super(ImmutableMap.of(
                CitadelMemoryModuleTypes.ROLL_TARGET.get(), MemoryStatus.VALUE_PRESENT
        ));
    }

    @Override
    protected void tick(ServerLevel level, Pebblet entity, long gameTime) {
        var brain = entity.getBrain();

        var rollTarget = brain.getMemory(CitadelMemoryModuleTypes.ROLL_TARGET.get());

        if (rollTarget.isPresent()) {
            var position = rollTarget.get().currentPosition();
            var direction = position.subtract(entity.position()).normalize();

            entity.addDeltaMovement(new Vec3(direction.x * 0.1f, 0.0f, direction.z * 0.1f));
            entity.setYRot((float) Mth.atan2(direction.z, direction.x));
        }
    }

    @Override
    protected void stop(ServerLevel level, Pebblet entity, long gameTime) {
        var brain = entity.getBrain();

        brain.eraseMemory(CitadelMemoryModuleTypes.ROLL_TARGET.get());
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Pebblet entity, long gameTime) {
        return true;
    }
}
