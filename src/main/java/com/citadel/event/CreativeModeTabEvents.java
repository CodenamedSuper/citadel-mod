package com.citadel.event;

import com.citadel.Citadel;
import com.citadel.registry.CitadelItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@EventBusSubscriber(modid = Citadel.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class CreativeModeTabEvents {

    @SubscribeEvent
    private static void onBuildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.insertAfter(
                    Items.PARROT_SPAWN_EGG.getDefaultInstance(),
                    CitadelItems.PEBBLET_SPAWN_EGG.get().getDefaultInstance(),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
        }
    }
}
