package com.chaosthedude.endermail.client.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EnderMailmanRenderFactory implements IRenderFactory {
	
	@Override
	public RenderEnderMailman createRenderFor(RenderManager manager) {
		return new RenderEnderMailman(manager);
	}

}
