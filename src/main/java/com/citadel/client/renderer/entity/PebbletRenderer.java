package com.citadel.client.renderer.entity;

import com.citadel.Citadel;
import com.citadel.client.model.entity.PebbletModel;
import com.citadel.client.renderer.entity.layer.PebbletEyesLayer;
import com.citadel.entity.pebblet.Pebblet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class PebbletRenderer extends MobRenderer<Pebblet, PebbletModel> {
    public static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(Citadel.MOD_ID, "textures/entity/pebblet/pebblet.png");

    public PebbletRenderer(EntityRendererProvider.Context context) {
        super(context, new PebbletModel(context.bakeLayer(PebbletModel.LAYER_LOCATION)), 0.5f);

        this.addLayer(new PebbletEyesLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(Pebblet entity) {
        return TEXTURE_LOCATION;
    }
}
