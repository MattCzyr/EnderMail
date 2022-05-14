package com.chaosthedude.endermail.registry;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.block.LockerBlock;
import com.chaosthedude.endermail.block.PackageBlock;
import com.chaosthedude.endermail.item.PackageControllerItem;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
@ObjectHolder(EnderMail.MODID)
public class EnderMailItems {

	@ObjectHolder(PackageBlock.NAME)
	public static final Item PACKAGE = null;
	
	@ObjectHolder(LockerBlock.NAME)
	public static final Item LOCKER = null;

	@ObjectHolder(PackageControllerItem.NAME)
	public static final PackageControllerItem PACKAGE_CONTROLLER = null;

	@ObjectHolder("packing_tape")
	public static final Item PACKING_TAPE = null;

	@ObjectHolder("stamp")
	public static final Item STAMP = null;

	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(
				new BlockItem(EnderMailBlocks.PACKAGE, new Properties().tab(CreativeModeTab.TAB_DECORATIONS)).setRegistryName(PackageBlock.NAME),
				new BlockItem(EnderMailBlocks.LOCKER, new Properties().tab(CreativeModeTab.TAB_DECORATIONS)).setRegistryName(LockerBlock.NAME),
				new PackageControllerItem().setRegistryName("package_controller"),
				new Item(new Properties().tab(CreativeModeTab.TAB_MISC)).setRegistryName("packing_tape"),
				new Item(new Properties().tab(CreativeModeTab.TAB_MISC)).setRegistryName("stamp"));
	}

}
