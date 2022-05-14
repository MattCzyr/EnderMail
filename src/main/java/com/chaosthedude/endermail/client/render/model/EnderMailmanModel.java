package com.chaosthedude.endermail.client.render.model;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.entity.EnderMailmanEntity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnderMailmanModel extends HumanoidModel<EnderMailmanEntity> {

	public static final ModelLayerLocation LOCATION = new ModelLayerLocation(new ResourceLocation(EnderMail.MODID, EnderMailmanEntity.NAME), "main");

	public boolean carrying;

	public EnderMailmanModel(ModelPart part) {
		super(part);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, -14.0F);
		PartDefinition part = mesh.getRoot();
		part.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
		part.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(0, 38).addBox(-4.0F, -12.0F, -4.0F, 8.0F, 3.0F, 8.0F).texOffs(0, 49).addBox(-5.0F, -9.0F, -9.0F, 10.0F, 1.0F, 14.0F), PartPose.offset(0.0F, -14.0F, 0.0F));
		part.addOrReplaceChild("body", CubeListBuilder.create().texOffs(32, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F), PartPose.offset(0.0F, -14.0F, 0.0F));
		part.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(56, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F), PartPose.offset(-5.0F, -12.0F, 0.0F));
		part.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(56, 0).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F), PartPose.offset(5.0F, -12.0F, 0.0F));
		part.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(56, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F), PartPose.offset(-2.0F, -5.0F, 0.0F));
		part.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(56, 0).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F), PartPose.offset(2.0F, -5.0F, 0.0F));
		return LayerDefinition.create(mesh, 64, 64);
	}

	@Override
	public void setupAnim(EnderMailmanEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		head.visible = true;
		hat.visible = true;
		body.xRot = 0.0F;
		body.y = -14.0F;
		body.z = -0.0F;
		rightLeg.xRot -= 0.0F;
		leftLeg.xRot -= 0.0F;
		rightArm.xRot = (float) ((double) rightArm.xRot * 0.5D);
		leftArm.xRot = (float) ((double) leftArm.xRot * 0.5D);
		rightLeg.xRot = (float) ((double) rightLeg.xRot * 0.5D);
		leftLeg.xRot = (float) ((double) leftLeg.xRot * 0.5D);
		if (rightArm.xRot > 0.4F) {
			rightArm.xRot = 0.4F;
		}

		if (leftArm.xRot > 0.4F) {
			leftArm.xRot = 0.4F;
		}

		if (rightArm.xRot < -0.4F) {
			rightArm.xRot = -0.4F;
		}

		if (leftArm.xRot < -0.4F) {
			leftArm.xRot = -0.4F;
		}

		if (rightLeg.xRot > 0.4F) {
			rightLeg.xRot = 0.4F;
		}

		if (leftLeg.xRot > 0.4F) {
			leftLeg.xRot = 0.4F;
		}

		if (rightLeg.xRot < -0.4F) {
			rightLeg.xRot = -0.4F;
		}

		if (leftLeg.xRot < -0.4F) {
			leftLeg.xRot = -0.4F;
		}

		if (carrying) {
			rightArm.xRot = -0.5F;
			leftArm.xRot = -0.5F;
			rightArm.zRot = 0.05F;
			leftArm.zRot = -0.05F;
		}

		rightLeg.z = 0.0F;
		leftLeg.z = 0.0F;
		rightLeg.y = -5.0F;
		leftLeg.y = -5.0F;
		head.z = -0.0F;
		head.y = -13.0F;
		hat.x = head.x;
		hat.y = head.y;
		hat.z = head.z;
		hat.xRot = head.xRot;
		hat.yRot = head.yRot;
		hat.zRot = head.zRot;

		rightArm.setPos(-5.0F, -12.0F, 0.0F);
		leftArm.setPos(5.0F, -12.0F, 0.0F);
	}

}