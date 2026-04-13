package com.citadel.registry;

import com.citadel.Citadel;
import com.citadel.entity.pebblet.ai.sensor.PebbletTargetingSensor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CitadelSensorTypes {
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(Registries.SENSOR_TYPE, Citadel.MOD_ID);

    public static final Supplier<SensorType<PebbletTargetingSensor>> PEBBLET_TARGETING_SENSOR = register("pebblet_targeting_sensor", PebbletTargetingSensor::new);

    private static <U extends Sensor<?>> Supplier<SensorType<U>> register(String name, Supplier<U> sensor) {
        return SENSOR_TYPES.register(name, () -> new SensorType<>(sensor));
    }
}
