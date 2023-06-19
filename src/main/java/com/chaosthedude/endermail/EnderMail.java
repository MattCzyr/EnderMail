package com.chaosthedude.endermail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chaosthedude.endermail.client.ClientEventHandler;
import com.chaosthedude.endermail.config.ConfigHandler;
import com.chaosthedude.endermail.gui.LockerScreen;
import com.chaosthedude.endermail.gui.PackageScreen;
import com.chaosthedude.endermail.item.PackageControllerItem;
import com.chaosthedude.endermail.network.ConfigureLockerPacket;
import com.chaosthedude.endermail.network.StampPackagePacket;
import com.chaosthedude.endermail.registry.EnderMailBlockEntities;
import com.chaosthedude.endermail.registry.EnderMailBlocks;
import com.chaosthedude.endermail.registry.EnderMailContainers;
import com.chaosthedude.endermail.registry.EnderMailEntities;
import com.chaosthedude.endermail.registry.EnderMailItems;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod(EnderMail.MODID)
public class EnderMail {

	public static final String MODID = "endermail";

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static SimpleChannel network;

	public EnderMail() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		
		EnderMailBlocks.BLOCK_DEFERRED.register(bus);
		EnderMailBlockEntities.BLOCK_ENTITY_DEFERRED.register(bus);
		EnderMailContainers.CONTAINER_DEFERRED.register(bus);
		EnderMailEntities.ENTITY_DEFERRED.register(bus);
		EnderMailItems.ITEM_DEFERRED.register(bus);

		bus.addListener(this::preInit);
		bus.addListener(this::buildCreativeTabContents);
		
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
		});
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.GENERAL_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigHandler.CLIENT_SPEC);

		MinecraftForge.EVENT_BUS.register(this);
	}

	private void preInit(FMLCommonSetupEvent event) {
		network = NetworkRegistry.newSimpleChannel(new ResourceLocation(EnderMail.MODID, EnderMail.MODID), () -> "1.0", s -> true, s -> true);
		network.registerMessage(0, StampPackagePacket.class, StampPackagePacket::toBytes, StampPackagePacket::new, StampPackagePacket::handle);
		network.registerMessage(1, ConfigureLockerPacket.class, ConfigureLockerPacket::toBytes, ConfigureLockerPacket::new, ConfigureLockerPacket::handle);
	}
	
	private void buildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			event.accept(new ItemStack(EnderMailItems.PACKAGE_CONTROLLER.get()));
			event.accept(new ItemStack(EnderMailItems.STAMP.get()));
		} else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
			event.accept(new ItemStack(EnderMailItems.PACKING_TAPE.get()));
		} else if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
			event.accept(new ItemStack(EnderMailItems.PACKAGE.get()));
			event.accept(new ItemStack(EnderMailItems.LOCKER.get()));
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	private void clientInit(FMLClientSetupEvent event) {
 		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
 		MenuScreens.register(EnderMailContainers.PACKAGE_CONTAINER.get(), PackageScreen::new);
 		MenuScreens.register(EnderMailContainers.LOCKER_CONTAINER.get(), LockerScreen::new);
 		
 		ItemProperties.register(EnderMailItems.PACKAGE_CONTROLLER.get(), new ResourceLocation(MODID, "state"), new ClampedItemPropertyFunction() {
			@Override
			public float unclampedCall(ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
				if (stack.getItem() == EnderMailItems.PACKAGE_CONTROLLER.get()) {
					PackageControllerItem packageController = (PackageControllerItem) stack.getItem();
					return 0.1F * packageController.getState(stack).getID(); // Value must be between 0.0 and 1.0
				}
				return 0.0F;
			}
		});
 	}

}
