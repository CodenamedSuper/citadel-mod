package com.citadel.client.renderer.entity.layer;

import com.citadel.Citadel;
import com.citadel.client.model.entity.PebbletModel;
import com.citadel.entity.pebblet.Pebblet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class PebbletEyesLayer extends EyesLayer<Pebblet, PebbletModel> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Citadel.MOD_ID, "textures/entity/pebblet/pebblet_eyes.png");
    public static final RenderType LAYER = RenderType.eyes(TEXTURE);

    public PebbletEyesLayer(RenderLayerParent<Pebblet, PebbletModel> renderer) {
        super(renderer);
    }

    @Override
    public RenderType renderType() {
        return LAYER;
    }
}
