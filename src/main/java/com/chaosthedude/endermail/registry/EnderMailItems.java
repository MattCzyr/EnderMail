package com.chaosthedude.endermail.registry;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.blocks.PackageBlock;
import com.chaosthedude.endermail.items.PackageControllerItem;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
@ObjectHolder(EnderMail.MODID)
public class EnderMailItems {

	@ObjectHolder(PackageBlock.NAME)
	public static final Item PACKAGE_ITEM = null;

	@ObjectHolder(PackageControllerItem.NAME)
	public static final PackageControllerItem PACKAGE_CONTROLLER = null;

	@ObjectHolder("packing_tape")
	public static final Item PACKING_TAPE = null;

	@ObjectHolder("stamp")
	public static final Item STAMP = null;

	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(new BlockItem(EnderMailBlocks.PACKAGE_BLOCK,
				new Properties().group(ItemGroup.DECORATIONS)).setRegistryName(PackageBlock.NAME),
				new PackageControllerItem().setRegistryName("package_controller"),
				new Item(new Properties().group(ItemGroup.MISC)).setRegistryName("packing_tape"),
				new Item(new Properties().group(ItemGroup.MISC)).setRegistryName("stamp"));
	}

}
