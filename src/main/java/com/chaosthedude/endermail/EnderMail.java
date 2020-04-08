package com.chaosthedude.endermail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chaosthedude.endermail.client.ClientProxy;
import com.chaosthedude.endermail.config.ConfigHandler;
import com.chaosthedude.endermail.network.StampPackagePacket;
import com.chaosthedude.endermail.proxy.CommonProxy;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(EnderMail.MODID)
public class EnderMail {

	public static final String MODID = "endermail";
	public static final String NAME = "Ender Mail";
	public static final String VERSION = "1.0.0";

	public static final Logger logger = LogManager.getLogger(MODID);

	public static SimpleChannel network;

	public static CommonProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new CommonProxy());

	public static EnderMail instance;

	public EnderMail() {
		instance = this;

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::preInit);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.GENERAL_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigHandler.CLIENT_SPEC);

		MinecraftForge.EVENT_BUS.register(this);
	}

	public void preInit(FMLCommonSetupEvent event) {
		network = NetworkRegistry.newSimpleChannel(new ResourceLocation(EnderMail.MODID, EnderMail.MODID), () -> "1.0", s -> true, s -> true);
		network.registerMessage(1, StampPackagePacket.class, StampPackagePacket::toBytes, StampPackagePacket::new, StampPackagePacket::handle);

		proxy.registerEvents();
		proxy.registerScreenFactories();
		proxy.registerRenderers();
	}

}
