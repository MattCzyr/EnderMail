package com.chaosthedude.endermail.client;

import com.chaosthedude.endermail.client.render.EnderMailmanRenderFactory;
import com.chaosthedude.endermail.entity.EntityEnderMailman;
import com.chaosthedude.endermail.proxy.CommonProxy;
import com.chaosthedude.endermail.registry.EnderMailBlocks;
import com.chaosthedude.endermail.registry.EnderMailItems;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void registerEvents() {
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
	}
	
	@Override
	public void registerRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityEnderMailman.class, new EnderMailmanRenderFactory());
	}

}
