package com.citadel.client.model.entity;

import com.citadel.Citadel;
import com.citadel.entity.pebblet.Pebblet;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.entity.animation.json.AnimationHolder;

public class PebbletModel extends HierarchicalModel<Pebblet> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Citadel.MOD_ID, "pebblet"), "main");
	public static final AnimationHolder WALK_ANIMATION = HierarchicalModel.getAnimation(ResourceLocation.fromNamespaceAndPath(Citadel.MOD_ID, "pebblet/walk"));
	public static final AnimationHolder ROLL_UP_ANIMATION = HierarchicalModel.getAnimation(ResourceLocation.fromNamespaceAndPath(Citadel.MOD_ID, "pebblet/roll_up"));
	public static final AnimationHolder ROLL_ANIMATION = HierarchicalModel.getAnimation(ResourceLocation.fromNamespaceAndPath(Citadel.MOD_ID, "pebblet/roll"));

	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart leftLeg;
	private final ModelPart rightLeg;

	public PebbletModel(ModelPart root) {
		this.root = root.getChild("root");
		this.head = this.root.getChild("head");
		this.leftLeg = this.root.getChild("left_leg");
		this.rightLeg = this.root.getChild("right_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
		root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.0F, 0.0F));
		root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 23).addBox(-1.0F, -1.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -5.0F, 0.0F));
		root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 23).mirror().addBox(-2.0F, -1.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-3.0F, -5.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(Pebblet entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animateWalk(WALK_ANIMATION, limbSwing, limbSwingAmount, 8.0f, 50.0f);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}