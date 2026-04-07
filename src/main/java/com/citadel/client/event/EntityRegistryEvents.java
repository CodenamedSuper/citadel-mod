package com.citadel.client.event;

import com.citadel.Citadel;
import com.citadel.client.model.entity.PebbletModel;
import com.citadel.client.renderer.entity.PebbletRenderer;
import com.citadel.registry.CitadelEntityTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Citadel.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class EntityRegistryEvents {

    @SubscribeEvent
    public static void onRegistryLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(PebbletModel.LAYER_LOCATION, PebbletModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(CitadelEntityTypes.PEBBLET.get(), PebbletRenderer::new);
    }
}
