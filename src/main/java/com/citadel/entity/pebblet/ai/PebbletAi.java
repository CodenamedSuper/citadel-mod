package com.citadel.entity.pebblet.ai;

import com.citadel.entity.pebblet.Pebblet;
import com.citadel.entity.pebblet.ai.behavior.RollUp;
import com.citadel.registry.CitadelActivities;
import com.citadel.registry.CitadelMemoryModuleTypes;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

import java.util.List;
import java.util.Set;

public class PebbletAi {
    public static final List<SensorType<? extends Sensor<? super Pebblet>>> SENSORS = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.HURT_BY
    );
    public static final List<MemoryModuleType<?>> MEMORIES = ImmutableList.of(
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.PATH,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.NEAREST_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.HURT_BY,
            MemoryModuleType.HURT_BY_ENTITY,
            MemoryModuleType.ATTACK_TARGET,
            CitadelMemoryModuleTypes.ROLL_TARGET_POS.get()
    );

    public static Brain<?> makeBrain(Brain<Pebblet> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initRollUpActivity(brain);
        initRollActivity(brain);

        brain.setCoreActivities(Set.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initCoreActivity(Brain<Pebblet> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink()
        ));
    }

    private static void initIdleActivity(Brain<Pebblet> brain) {
        var hurtByBehavior = StartAttacking.create(Pebblet::getHurtBy);

        var lookAtPlayerBehavior = SetEntityLookTarget.create(EntityType.PLAYER, 8.0f);

        var walkOptionsBehavior = new RunOne<Pebblet>(ImmutableList.of(
                Pair.of(RandomStroll.stroll(1.0f), 2),
                Pair.of(SetWalkTargetFromLookTarget.create(1.0f, 1), 2),
                Pair.of(new DoNothing(30, 60), 1)
        ));

        brain.addActivity(
                Activity.IDLE,
                ImmutableList.of(
                        Pair.of(1, hurtByBehavior),
                        Pair.of(2, lookAtPlayerBehavior),
                        Pair.of(3, walkOptionsBehavior)
                )
        );
    }

    private static void initRollUpActivity(Brain<Pebblet> brain) {
        brain.addActivityWithConditions(
                CitadelActivities.ROLL_UP.get(),
                ImmutableList.of(
                        Pair.of(1, new RollUp())
                ),
                ImmutableSet.of(
                        Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT),
                        Pair.of(CitadelMemoryModuleTypes.ROLL_TARGET_POS.get(), MemoryStatus.VALUE_ABSENT)
                )
        );
    }

    private static void initRollActivity(Brain<Pebblet> brain) {
        brain.addActivityWithConditions(
                CitadelActivities.ROLL.get(),
                ImmutableList.of(

                ),
                ImmutableSet.of(
                        Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT),
                        Pair.of(CitadelMemoryModuleTypes.ROLL_TARGET_POS.get(), MemoryStatus.VALUE_PRESENT)
                )
        );
    }

    public static void updateActivity(Pebblet entity) {
        entity.getBrain().setActiveActivityToFirstValid(List.of(
                CitadelActivities.ROLL.get(),
                CitadelActivities.ROLL_UP.get(),
                Activity.IDLE
        ));
    }
}
