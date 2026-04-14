package com.citadel.registry;

import com.citadel.Citadel;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CitadelCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Citadel.MOD_ID);

    public static final Supplier<CreativeModeTab> MAIN = CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("creative_mode_tab.citadel.main"))
            .icon(() -> CitadelItems.ICON.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(CitadelItems.PEBBLET_SPAWN_EGG.get());
            })
            .build()
    );
}
