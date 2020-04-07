package com.chaosthedude.endermail.client;

import com.chaosthedude.endermail.client.render.EnderMailmanRenderFactory;
import com.chaosthedude.endermail.entity.EnderMailmanEntity;
import com.chaosthedude.endermail.gui.PackageScreen;
import com.chaosthedude.endermail.proxy.CommonProxy;
import com.chaosthedude.endermail.registry.EnderMailContainers;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void registerEvents() {
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
	}
	
	@Override
	public void registerScreenFactories() {
		ScreenManager.registerFactory(EnderMailContainers.PACKAGE_CONTAINER, PackageScreen::new);
	}
	
	@Override
	public void registerRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EnderMailmanEntity.class, new EnderMailmanRenderFactory());
	}

}
