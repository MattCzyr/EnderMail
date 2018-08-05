package com.chaosthedude.endermail.client.render;

import java.util.Random;

import com.chaosthedude.endermail.client.render.model.ModelEnderMailman;
import com.chaosthedude.endermail.entity.EntityEnderMailman;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelEnderman;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerEndermanEyes;
import net.minecraft.client.renderer.entity.layers.LayerHeldBlock;
import net.minecraft.util.ResourceLocation;

public class RenderEnderMailman extends RenderLiving<EntityEnderMailman> {

	private static final ResourceLocation texture = new ResourceLocation("endermail", "textures/models/ender_mailman.png");

	public RenderEnderMailman(RenderManager renderManager) {
		super(renderManager, new ModelEnderMailman(0.0F), 0.5F);
		addLayer(new LayerHeldPackage(this));
	}

	@Override
	public ModelEnderMailman getMainModel() {
		return (ModelEnderMailman) super.getMainModel();
	}

	@Override
	public void doRender(EntityEnderMailman entityEnderMailman, double x, double y, double z, float entityYaw, float partialTicks) {
		ModelEnderMailman model = getMainModel();
		model.isCarrying = entityEnderMailman.isCarryingPackage();

		super.doRender(entityEnderMailman, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityEnderMailman soul) {
		return texture;
	}

}
