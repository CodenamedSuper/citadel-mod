package com.citadel;

import com.citadel.registry.*;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(Citadel.MOD_ID)
public class Citadel {
    public static final String MOD_ID = "citadel";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Citadel(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        CitadelItems.ITEMS.register(modEventBus);
        CitadelBlocks.BLOCKS.register(modEventBus);
        CitadelEntityTypes.ENTITY_TYPES.register(modEventBus);
        CitadelActivities.ACTIVITIES.register(modEventBus);
        CitadelMemoryModuleTypes.MEMORY_MODULE_TYPES.register(modEventBus);
        CitadelSensorTypes.SENSOR_TYPES.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Welcome to the Citadel");
    }
}
