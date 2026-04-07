package com.citadel.entity.pebblet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

import java.util.List;
import java.util.Set;

public class PebbletAi {
    public static final List<SensorType<? extends Sensor<? super Pebblet>>> SENSORS = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES
    );
    public static final List<MemoryModuleType<?>> MEMORIES = ImmutableList.of(
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.PATH,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES
    );

    public static Brain<?> makeBrain(Brain<Pebblet> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);

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
        brain.addActivityWithConditions(
                Activity.IDLE,
                ImmutableList.of(
                        Pair.of(1, SetEntityLookTarget.create(EntityType.PLAYER, 8.0f)),
                        Pair.of(2, RandomStroll.stroll(1.0f))
                ),
                ImmutableSet.of()
        );
    }

    public static void updateActivity(Pebblet entity) {
        entity.getBrain().setActiveActivityToFirstValid(List.of(
                Activity.IDLE
        ));
    }
}
