package com.chaosthedude.endermail.client.render;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.entity.EntityEnderMailman;
import com.chaosthedude.endermail.registry.EnderMailBlocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerHeldPackage implements LayerRenderer<EntityEnderMailman> {
	
	private final RenderEnderMailman renderer;

	public LayerHeldPackage(RenderEnderMailman renderer) {
		this.renderer = renderer;
	}

	@Override
	public void doRenderLayer(EntityEnderMailman entityEnderMailman, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if (entityEnderMailman.isCarryingPackage()) {
			BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
			GlStateManager.enableRescaleNormal();
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 0.6875F, -0.75F);
			GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(0.25F, 0.1875F, 0.25F);
			float f = 0.5F;
			GlStateManager.scale(-0.5F, -0.5F, 0.5F);
			int i = entityEnderMailman.getBrightnessForRender();
			int j = i % 65536;
			int k = i / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			renderer.bindTexture(new ResourceLocation(EnderMail.MODID, "textures/blocks/package_side_1.png"));
			dispatcher.renderBlockBrightness(EnderMailBlocks.package_block.getStampedState(), 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.disableRescaleNormal();
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}

}
