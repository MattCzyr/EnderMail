package com.chaosthedude.endermail.client.render;

import com.chaosthedude.endermail.client.render.model.EnderMailmanModel;
import com.chaosthedude.endermail.entity.EnderMailmanEntity;
import com.chaosthedude.endermail.registry.EnderMailBlocks;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeldPackageLayer extends LayerRenderer<EnderMailmanEntity, EnderMailmanModel> {

	public HeldPackageLayer(EnderMailmanRenderer renderer) {
		super(renderer);
	}

	@Override
	public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, EnderMailmanEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		if (entity.isCarryingPackage()) {
			matrixStack.push();
			matrixStack.translate(0.0D, 0.6875D, -0.75D);
			matrixStack.rotate(Vector3f.XP.rotationDegrees(20.0F));
			matrixStack.rotate(Vector3f.YP.rotationDegrees(45.0F));
			matrixStack.translate(0.25D, 0.1875D, 0.25D);
			float f = 0.5F;
			matrixStack.scale(-0.5F, -0.5F, 0.5F);
			matrixStack.rotate(Vector3f.YP.rotationDegrees(90.0F));
			Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(EnderMailBlocks.PACKAGE.getStampedState(), matrixStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
			matrixStack.pop();
		}
	}

}
