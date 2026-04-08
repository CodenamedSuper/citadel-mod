package com.citadel.registry;

import com.citadel.Citadel;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.schedule.Activity;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

public class CitadelActivities {
    public static final DeferredRegister<Activity> ACTIVITIES = DeferredRegister.create(Registries.ACTIVITY, Citadel.MOD_ID);

    public static final Supplier<Activity> ROLL_UP = ACTIVITIES.register("roll_up", () -> new Activity("roll_up"));
    public static final Supplier<Activity> ROLL = ACTIVITIES.register("roll", () -> new Activity("roll"));
}
