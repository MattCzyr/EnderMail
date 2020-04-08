package com.chaosthedude.endermail.client.render.model;

import com.chaosthedude.endermail.entity.EnderMailmanEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnderMailmanModel extends BipedModel<EnderMailmanEntity> {

	public ModelRenderer brim;
	public ModelRenderer hat;

	public boolean isCarrying;

	public EnderMailmanModel(float scale) {
		super(0.0F, -14.0F, 64, 64);
		float f = -14.0F;
		bipedHeadwear = new ModelRenderer(this, 0, 16);
		bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, scale - 0.5F);
		bipedHeadwear.setRotationPoint(0.0F, -14.0F, 0.0F);

		bipedBody = new ModelRenderer(this, 32, 16);
		bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, scale);
		bipedBody.setRotationPoint(0.0F, -14.0F, 0.0F);

		bipedRightArm = new ModelRenderer(this, 56, 0);
		bipedRightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 30, 2, scale);
		bipedRightArm.setRotationPoint(-3.0F, -12.0F, 0.0F);

		bipedLeftArm = new ModelRenderer(this, 56, 0);
		bipedLeftArm.mirror = true;
		bipedLeftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 30, 2, scale);
		bipedLeftArm.setRotationPoint(5.0F, -12.0F, 0.0F);

		bipedRightLeg = new ModelRenderer(this, 56, 0);
		bipedRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 30, 2, scale);
		bipedRightLeg.setRotationPoint(-2.0F, -2.0F, 0.0F);

		bipedLeftLeg = new ModelRenderer(this, 56, 0);
		bipedLeftLeg.mirror = true;
		bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 30, 2, scale);
		bipedLeftLeg.setRotationPoint(2.0F, -2.0F, 0.0F);

		brim = new ModelRenderer(this, 0, 49);
		brim.addBox(-5.0F, -9.0F, -9.0F, 10, 1, 14);
		brim.setRotationPoint(0.0F, -14.0F, 0.0F);

		hat = new ModelRenderer(this, 0, 38);
		hat.addBox(-4.0F, -12.0F, -4.0F, 8, 3, 8);
		hat.setRotationPoint(0.0F, -14.0F, 0.0F);
	}

	@Override
	public void setRotationAngles(EnderMailmanEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		this.bipedHead.showModel = true;
		float f = -14.0F;
		bipedBody.rotateAngleX = 0.0F;
		bipedBody.rotationPointY = -14.0F;
		bipedBody.rotationPointZ = -0.0F;
		bipedRightLeg.rotateAngleX -= 0.0F;
		bipedLeftLeg.rotateAngleX -= 0.0F;
		bipedRightArm.rotateAngleX = (float) ((double) this.bipedRightArm.rotateAngleX * 0.5D);
		bipedLeftArm.rotateAngleX = (float) ((double) this.bipedLeftArm.rotateAngleX * 0.5D);
		bipedRightLeg.rotateAngleX = (float) ((double) this.bipedRightLeg.rotateAngleX * 0.5D);
		bipedLeftLeg.rotateAngleX = (float) ((double) this.bipedLeftLeg.rotateAngleX * 0.5D);
		float f1 = 0.4F;
		if (bipedRightArm.rotateAngleX > 0.4F) {
			bipedRightArm.rotateAngleX = 0.4F;
		}

		if (bipedLeftArm.rotateAngleX > 0.4F) {
			bipedLeftArm.rotateAngleX = 0.4F;
		}

		if (bipedRightArm.rotateAngleX < -0.4F) {
			bipedRightArm.rotateAngleX = -0.4F;
		}

		if (bipedLeftArm.rotateAngleX < -0.4F) {
			bipedLeftArm.rotateAngleX = -0.4F;
		}

		if (bipedRightLeg.rotateAngleX > 0.4F) {
			bipedRightLeg.rotateAngleX = 0.4F;
		}

		if (bipedLeftLeg.rotateAngleX > 0.4F) {
			bipedLeftLeg.rotateAngleX = 0.4F;
		}

		if (bipedRightLeg.rotateAngleX < -0.4F) {
			bipedRightLeg.rotateAngleX = -0.4F;
		}

		if (bipedLeftLeg.rotateAngleX < -0.4F) {
			bipedLeftLeg.rotateAngleX = -0.4F;
		}

		if (isCarrying) {
			bipedRightArm.rotateAngleX = -0.5F;
			bipedLeftArm.rotateAngleX = -0.5F;
			bipedRightArm.rotateAngleZ = 0.05F;
			bipedLeftArm.rotateAngleZ = -0.05F;
		}

		bipedRightArm.rotationPointZ = 0.0F;
		bipedLeftArm.rotationPointZ = 0.0F;
		bipedRightLeg.rotationPointZ = 0.0F;
		bipedLeftLeg.rotationPointZ = 0.0F;
		bipedRightLeg.rotationPointY = -5.0F;
		bipedLeftLeg.rotationPointY = -5.0F;
		bipedHead.rotationPointZ = -0.0F;
		bipedHead.rotationPointY = -13.0F;
		bipedHeadwear.rotationPointX = this.bipedHead.rotationPointX;
		bipedHeadwear.rotationPointY = this.bipedHead.rotationPointY;
		bipedHeadwear.rotationPointZ = this.bipedHead.rotationPointZ;
		bipedHeadwear.rotateAngleX = this.bipedHead.rotateAngleX;
		bipedHeadwear.rotateAngleY = this.bipedHead.rotateAngleY;
		bipedHeadwear.rotateAngleZ = this.bipedHead.rotateAngleZ;

		float f3 = -14.0F;
		bipedRightArm.setRotationPoint(-5.0F, -12.0F, 0.0F);
		bipedLeftArm.setRotationPoint(5.0F, -12.0F, 0.0F);
		brim.rotationPointX = bipedHead.rotationPointX;
		brim.rotationPointY = bipedHead.rotationPointY;
		brim.rotationPointZ = bipedHead.rotationPointZ;
		brim.rotateAngleX = bipedHead.rotateAngleX;
		brim.rotateAngleY = bipedHead.rotateAngleY;
		brim.rotateAngleZ = bipedHead.rotateAngleZ;
		hat.rotationPointX = bipedHead.rotationPointX;
		hat.rotationPointY = bipedHead.rotationPointY;
		hat.rotationPointZ = bipedHead.rotationPointZ;
		hat.rotateAngleX = bipedHead.rotateAngleX;
		hat.rotateAngleY = bipedHead.rotateAngleY;
		hat.rotateAngleZ = bipedHead.rotateAngleZ;
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		super.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		brim.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		hat.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

}