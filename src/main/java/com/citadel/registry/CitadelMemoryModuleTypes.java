package com.citadel.registry;

import com.citadel.Citadel;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;
import java.util.function.Supplier;

public class CitadelMemoryModuleTypes {
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPES = DeferredRegister.create(Registries.MEMORY_MODULE_TYPE, Citadel.MOD_ID);

    public static final Supplier<MemoryModuleType<Vec3>> ROLL_TARGET = register("roll_target", Vec3.CODEC);
    public static final Supplier<MemoryModuleType<Unit>> ROLL_COOLDOWN = register("roll_cooldown", Unit.CODEC);

    private static <T> Supplier<MemoryModuleType<T>> register(String name) {
        return register(name, Optional.empty());
    }

    private static <T> Supplier<MemoryModuleType<T>> register(String name, Codec<T> codec) {
        return register(name, Optional.of(codec));
    }

    private static <T> Supplier<MemoryModuleType<T>> register(String name, Optional<Codec<T>> codec) {
        return MEMORY_MODULE_TYPES.register(name, () -> new MemoryModuleType<>(codec));
    }
}
