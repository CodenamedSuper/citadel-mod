package com.citadel.entity.pebblet.ai.sensor;

import com.citadel.entity.pebblet.Pebblet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;

import java.util.Collection;
import java.util.Set;

public class PebbletTargetingSensor extends Sensor<Pebblet> {

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of(
                MemoryModuleType.NEAREST_LIVING_ENTITIES,
                MemoryModuleType.NEAREST_ATTACKABLE
        );
    }

    @Override
    protected void doTick(ServerLevel level, Pebblet entity) {
        Brain<Pebblet> brain = entity.getBrain();

        brain.getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES)
                .stream()
                .flatMap(Collection::stream)
                .filter(EntitySelector.NO_CREATIVE_OR_SPECTATOR)
                .filter(e -> Sensor.isEntityAttackable(entity, e))
                .findFirst()
                .ifPresentOrElse(
                        e -> brain.setMemory(MemoryModuleType.NEAREST_ATTACKABLE, e),
                        () -> brain.eraseMemory(MemoryModuleType.NEAREST_ATTACKABLE)
                );
    }
}
