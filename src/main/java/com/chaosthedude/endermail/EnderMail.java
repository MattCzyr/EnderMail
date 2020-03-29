package com.chaosthedude.endermail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chaosthedude.endermail.config.ConfigHandler;
import com.chaosthedude.endermail.entity.EntityEnderMailman;
import com.chaosthedude.endermail.gui.GuiHandler;
import com.chaosthedude.endermail.network.PacketSpawnMailman;
import com.chaosthedude.endermail.network.PacketStampPackage;
import com.chaosthedude.endermail.proxy.CommonProxy;
import com.chaosthedude.endermail.registry.EnderMailBlocks;
import com.chaosthedude.endermail.registry.EnderMailItems;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = EnderMail.MODID, name = EnderMail.NAME, version = EnderMail.VERSION, acceptedMinecraftVersions = "[1.12.2]")

public class EnderMail {

	public static final String MODID = "endermail";
	public static final String NAME = "Ender Mail";
	public static final String VERSION = "1.0.0";

	public static final Logger logger = LogManager.getLogger(MODID);

	public static SimpleNetworkWrapper network;

	@SidedProxy(clientSide = "com.chaosthedude.endermail.client.ClientProxy", serverSide = "com.chaosthedude.endermail.proxy.CommonProxy")
	public static CommonProxy proxy;

	@Instance("endermail")
	public static EnderMail instance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigHandler.loadConfig(event.getSuggestedConfigurationFile());
		
		EnderMailBlocks.register();
		EnderMailItems.register();

		EntityRegistry.registerModEntity(new ResourceLocation(MODID, EntityEnderMailman.NAME), EntityEnderMailman.class, EntityEnderMailman.NAME, 0, this, 64, 2, true);

		network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		network.registerMessage(PacketSpawnMailman.Handler.class, PacketSpawnMailman.class, 0, Side.SERVER);
		network.registerMessage(PacketStampPackage.Handler.class, PacketStampPackage.class, 1, Side.SERVER);

		proxy.registerEvents();
		proxy.registerModels();
		proxy.registerRenderers();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
	}

}
