package com.chaosthedude.endermail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chaosthedude.endermail.client.ClientEventHandler;
import com.chaosthedude.endermail.client.render.EnderMailmanRenderFactory;
import com.chaosthedude.endermail.config.ConfigHandler;
import com.chaosthedude.endermail.entity.EnderMailmanEntity;
import com.chaosthedude.endermail.gui.LockerScreen;
import com.chaosthedude.endermail.gui.PackageScreen;
import com.chaosthedude.endermail.items.PackageControllerItem;
import com.chaosthedude.endermail.network.ConfigureLockerPacket;
import com.chaosthedude.endermail.network.StampPackagePacket;
import com.chaosthedude.endermail.registry.EnderMailContainers;
import com.chaosthedude.endermail.registry.EnderMailEntities;
import com.chaosthedude.endermail.registry.EnderMailItems;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(EnderMail.MODID)
public class EnderMail {

	public static final String MODID = "endermail";
	public static final String NAME = "Ender Mail";
	public static final String VERSION = "1.2.1";

	public static final Logger logger = LogManager.getLogger(MODID);

	public static SimpleChannel network;

	public static EnderMail instance;

	public EnderMail() {
		instance = this;

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::preInit);
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
		});
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.GENERAL_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigHandler.CLIENT_SPEC);

		MinecraftForge.EVENT_BUS.register(this);
	}

	public void preInit(FMLCommonSetupEvent event) {
		network = NetworkRegistry.newSimpleChannel(new ResourceLocation(EnderMail.MODID, EnderMail.MODID), () -> "1.0", s -> true, s -> true);
		network.registerMessage(1, StampPackagePacket.class, StampPackagePacket::toBytes, StampPackagePacket::new, StampPackagePacket::handle);
		network.registerMessage(2, ConfigureLockerPacket.class, ConfigureLockerPacket::toBytes, ConfigureLockerPacket::new, ConfigureLockerPacket::handle);
		
		GlobalEntityTypeAttributes.put(EnderMailEntities.ENDER_MAILMAN_TYPE, EnderMailmanEntity.createAttributes().create());
	}
	
	@OnlyIn(Dist.CLIENT)
	public void clientInit(FMLClientSetupEvent event) {
 		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
 		ScreenManager.registerFactory(EnderMailContainers.PACKAGE_CONTAINER, PackageScreen::new);
 		ScreenManager.registerFactory(EnderMailContainers.LOCKER_CONTAINER, LockerScreen::new);
 		RenderingRegistry.registerEntityRenderingHandler(EnderMailEntities.ENDER_MAILMAN_TYPE, new EnderMailmanRenderFactory());
 		
 		ItemModelsProperties.registerProperty(EnderMailItems.PACKAGE_CONTROLLER, new ResourceLocation("state"), new IItemPropertyGetter() {
			@Override
			public float call(ItemStack stack, ClientWorld world, LivingEntity entity) {
				if (stack.getItem() == EnderMailItems.PACKAGE_CONTROLLER) {
					PackageControllerItem packageController = (PackageControllerItem) stack.getItem();
					return packageController.getState(stack).getID();
				}
				return 0F;
			}
		});
 	}

}
