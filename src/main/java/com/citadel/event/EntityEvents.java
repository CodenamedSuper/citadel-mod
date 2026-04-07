package com.citadel.event;

import com.citadel.Citadel;
import com.citadel.entity.pebblet.Pebblet;
import com.citadel.registry.CitadelEntityTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = Citadel.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class EntityEvents {

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(CitadelEntityTypes.PEBBLET.get(), Pebblet.createPebbletAttributes().build());
    }
}
