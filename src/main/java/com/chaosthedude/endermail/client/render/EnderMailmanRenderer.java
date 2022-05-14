package com.chaosthedude.endermail.client.render;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.client.render.model.EnderMailmanModel;
import com.chaosthedude.endermail.entity.EnderMailmanEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnderMailmanRenderer extends MobRenderer<EnderMailmanEntity, EnderMailmanModel> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(EnderMail.MODID, "textures/models/ender_mailman.png");

	public EnderMailmanRenderer(EntityRendererProvider.Context context) {
		super(context, new EnderMailmanModel(context.bakeLayer(EnderMailmanModel.LOCATION)), 0.5F);
		addLayer(new HeldPackageLayer(this));
	}

	@Override
	public void render(EnderMailmanEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		EnderMailmanModel model = getModel();
		model.carrying = entity.isCarryingPackage();
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(EnderMailmanEntity entity) {
		return TEXTURE;
	}

}
