package com.chaosthedude.endermail.registry;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.blocks.PackageBlock;
import com.chaosthedude.endermail.blocks.te.PackageTileEntity;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
@ObjectHolder(EnderMail.MODID)
public class EnderMailBlocks {

	@ObjectHolder(PackageBlock.NAME)
	public static final PackageBlock PACKAGE_BLOCK = null;

	@ObjectHolder(PackageTileEntity.NAME)
	public static final TileEntityType<?> PACKAGE_TE_TYPE = null;

	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		event.getRegistry().register(new PackageBlock().setRegistryName(PackageBlock.NAME));
	}

	@SubscribeEvent
	public static void registerTileEntities(final RegistryEvent.Register<TileEntityType<?>> event) {
		event.getRegistry().register(TileEntityType.Builder.create(PackageTileEntity::new, EnderMailBlocks.PACKAGE_BLOCK).build(null).setRegistryName(PackageTileEntity.NAME));
	}

}
