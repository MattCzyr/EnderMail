package com.chaosthedude.endermail.client.render;

import com.chaosthedude.endermail.client.render.model.EnderMailmanModel;
import com.chaosthedude.endermail.entity.EnderMailmanEntity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnderMailmanRenderer extends MobRenderer<EnderMailmanEntity, EnderMailmanModel> implements IEntityRenderer<EnderMailmanEntity, EnderMailmanModel> {

	private static final ResourceLocation texture = new ResourceLocation("endermail", "textures/models/ender_mailman.png");

	public EnderMailmanRenderer(EntityRendererManager renderManager) {
		super(renderManager, new EnderMailmanModel(0.0F), 0.5F);
		addLayer(new HeldPackageLayer(this));
	}

	@Override
	public EnderMailmanModel getEntityModel() {
		return (EnderMailmanModel) super.getEntityModel();
	}

	@Override
	public void doRender(EnderMailmanEntity entityEnderMailman, double x, double y, double z, float entityYaw, float partialTicks) {
		EnderMailmanModel model = getEntityModel();
		model.isCarrying = entityEnderMailman.isCarryingPackage();

		super.doRender(entityEnderMailman, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(EnderMailmanEntity soul) {
		return texture;
	}

}
