package com.chaosthedude.endermail.client.render;

import com.chaosthedude.endermail.client.render.model.EnderMailmanModel;
import com.chaosthedude.endermail.entity.EnderMailmanEntity;
import com.chaosthedude.endermail.registry.EnderMailBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;

@OnlyIn(Dist.CLIENT)
public class HeldPackageLayer extends RenderLayer<EnderMailmanEntity, EnderMailmanModel> {

	public HeldPackageLayer(EnderMailmanRenderer renderer) {
		super(renderer);
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, EnderMailmanEntity entity, float p_116643_, float p_116644_, float p_116645_, float p_116646_, float p_116647_, float p_116648_) {
		if (entity.isCarryingPackage()) {
			poseStack.pushPose();
			poseStack.translate(0.0D, 0.6875D, -0.75D);
			poseStack.mulPose(Axis.XP.rotationDegrees(20.0F));
			poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
			poseStack.translate(0.25D, 0.1875D, 0.25D);
			poseStack.scale(-0.5F, -0.5F, 0.5F);
			poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
			Minecraft.getInstance().getBlockRenderer().renderSingleBlock(EnderMailBlocks.PACKAGE.get().getStampedState(), poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
			poseStack.popPose();
		}
	}

}
