package com.chaosthedude.endermail.client.render;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.client.render.model.EnderMailmanModel;
import com.chaosthedude.endermail.entity.EnderMailmanEntity;
import com.chaosthedude.endermail.registry.EnderMailBlocks;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeldPackageLayer extends LayerRenderer<EnderMailmanEntity, EnderMailmanModel> {
	
	private final EnderMailmanRenderer renderer;
	
	public HeldPackageLayer(EnderMailmanRenderer renderer) {
		super(renderer);
		this.renderer = renderer;
	}

	@Override
	public void render(EnderMailmanEntity entityEnderMailman, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if (entityEnderMailman.isCarryingPackage()) {
			BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
			GlStateManager.enableRescaleNormal();
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, 0.6875F, -0.75F);
			GlStateManager.rotatef(20.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(45.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translatef(0.25F, 0.1875F, 0.25F);
			GlStateManager.scalef(-0.5F, -0.5F, 0.5F);
			int i = entityEnderMailman.getBrightnessForRender();
			int j = i % 65536;
			int k = i / 65536;
			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)j, (float)k);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			renderer.bindTexture(new ResourceLocation(EnderMail.MODID, "textures/blocks/package_side_1.png"));
			dispatcher.renderBlockBrightness(EnderMailBlocks.PACKAGE_BLOCK.getStampedState(), 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.disableRescaleNormal();
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}

}
