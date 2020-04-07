package com.chaosthedude.endermail.client.render;

import com.chaosthedude.endermail.entity.EnderMailmanEntity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;

@OnlyIn(Dist.CLIENT)
public class EnderMailmanRenderFactory implements IRenderFactory<EnderMailmanEntity> {

	@Override
	public EntityRenderer<EnderMailmanEntity> createRenderFor(EntityRendererManager manager) {
		return new EnderMailmanRenderer(manager);
	}

}
