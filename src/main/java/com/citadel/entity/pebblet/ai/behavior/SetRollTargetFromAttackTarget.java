package com.citadel.entity.pebblet.ai.behavior;

import com.citadel.entity.pebblet.Pebblet;
import com.citadel.registry.CitadelMemoryModuleTypes;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

import java.util.Optional;

public class SetRollTargetFromAttackTarget extends Behavior<Pebblet> {

    public SetRollTargetFromAttackTarget() {
        super(ImmutableMap.of(
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

        brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(
                target,
                true
        ));
        this.tryGetRollTarget(brain, target);
    }

    private void tryGetRollTarget(Brain<Pebblet> brain, LivingEntity target) {
        Optional<NearestVisibleLivingEntities> nearestVisibleLivingEntitiesMemory = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
        if (nearestVisibleLivingEntitiesMemory.isEmpty()) return;

        NearestVisibleLivingEntities visibleEntities = nearestVisibleLivingEntitiesMemory.get();

        if (visibleEntities.contains(target)) {
            brain.setMemory(CitadelMemoryModuleTypes.ROLL_TARGET.get(), target.position());
        }
    }
}
