package com.citadel.registry;

import com.citadel.Citadel;
import com.citadel.entity.pebblet.Pebblet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CitadelEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Citadel.MOD_ID);

    public static final Supplier<EntityType<Pebblet>> PEBBLET = ENTITY_TYPES.register(
            "pebblet",
            () -> EntityType.Builder.of(Pebblet::new, MobCategory.MONSTER)
                    .sized(0.5f, 0.8125f)
                    .eyeHeight(0.5f)
                    .clientTrackingRange(8)
                    .build("pebblet")
    );
}
