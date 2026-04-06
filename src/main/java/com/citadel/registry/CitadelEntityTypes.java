package com.citadel.registry;

import com.citadel.Citadel;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CitadelEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Citadel.MOD_ID);
}
