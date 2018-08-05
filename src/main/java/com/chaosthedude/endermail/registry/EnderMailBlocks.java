package com.chaosthedude.endermail.registry;

import java.util.ArrayList;
import java.util.List;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.blocks.BlockPackage;
import com.chaosthedude.endermail.blocks.BlockSealedPackage;
import com.chaosthedude.endermail.blocks.BlockStampedPackage;
import com.chaosthedude.endermail.blocks.te.TileEntityPackage;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@EventBusSubscriber(modid = EnderMail.MODID)
public class EnderMailBlocks {
	
	public static final List<Block> REGISTRY = new ArrayList<Block>();

	public static BlockPackage blockPackage;
	public static BlockSealedPackage blockSealedPackage;
	public static BlockStampedPackage blockStampedPackage;

	public static void register() {
		blockPackage = registerBlock(new BlockPackage(), BlockPackage.NAME);
		blockSealedPackage = registerBlock(new BlockSealedPackage(), BlockSealedPackage.NAME);
		blockStampedPackage = registerBlock(new BlockStampedPackage(), BlockStampedPackage.NAME);

		GameRegistry.registerTileEntity(TileEntityPackage.class, new ResourceLocation(TileEntityPackage.NAME));
	}

	protected static <T extends Block> T registerBlock(T blockType, String name) {
		T block = blockType;
		block.setRegistryName(name);
		REGISTRY.add(block);

		return block;
	}
	
	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> e) {
		for (Block block : REGISTRY) {
			e.getRegistry().register(block);
		}
	}

}
