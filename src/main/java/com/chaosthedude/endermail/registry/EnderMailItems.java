package com.chaosthedude.endermail.registry;

import java.util.ArrayList;
import java.util.List;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.items.ItemPackageController;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderMail.MODID)
public class EnderMailItems {
	
	public static final List<Item> REGISTRY = new ArrayList<Item>();
	
	public static ItemPackageController packageController;
	public static Item packingTape;
	public static Item stamp;

	public static void register() {
		packageController = registerItem(new ItemPackageController(), "package_controller");
		packingTape = registerItem(new Item().setUnlocalizedName(EnderMail.MODID + ":" + "packing_tape").setCreativeTab(CreativeTabs.MISC), "packing_tape");
		stamp = registerItem(new Item().setUnlocalizedName(EnderMail.MODID + "." + "stamp").setCreativeTab(CreativeTabs.MISC), "stamp");
	}
	
	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> e) {
		for (Item item : REGISTRY) {
			e.getRegistry().register(item);
		}
		
		for (Block block : EnderMailBlocks.REGISTRY) {
			e.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
		}
	}

	protected static <T extends Item> T registerItem(T itemType, String name) {
		T item = itemType;
		item.setRegistryName(name);
		REGISTRY.add(item);

		return item;
	}

}
