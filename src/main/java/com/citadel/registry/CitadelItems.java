package com.citadel.registry;

import com.citadel.Citadel;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CitadelItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(Citadel.MOD_ID);

    public static final Supplier<Item> PEBBLET_SPAWN_EGG = ITEMS.register("pebblet_spawn_egg", () -> new DeferredSpawnEggItem(
            CitadelEntityTypes.PEBBLET,
            11382189,
            15329769,
            new Item.Properties()
    ));
}
