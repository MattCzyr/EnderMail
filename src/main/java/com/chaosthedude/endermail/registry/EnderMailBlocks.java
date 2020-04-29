package com.chaosthedude.endermail.registry;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.blocks.LockerBlock;
import com.chaosthedude.endermail.blocks.PackageBlock;
import com.chaosthedude.endermail.blocks.te.LockerTileEntity;
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
	public static final PackageBlock PACKAGE = null;

	@ObjectHolder(PackageTileEntity.NAME)
	public static final TileEntityType<?> PACKAGE_TE_TYPE = null;
	
	@ObjectHolder(LockerBlock.NAME)
	public static final LockerBlock LOCKER = null;
	
	@ObjectHolder(LockerTileEntity.NAME)
	public static final TileEntityType<?> LOCKER_TE_TYPE = null;

	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(
				new PackageBlock().setRegistryName(PackageBlock.NAME),
				new LockerBlock().setRegistryName(LockerBlock.NAME)
		);
	}

	@SubscribeEvent
	public static void registerTileEntities(final RegistryEvent.Register<TileEntityType<?>> event) {
		event.getRegistry().registerAll(
				TileEntityType.Builder.create(PackageTileEntity::new, EnderMailBlocks.PACKAGE).build(null).setRegistryName(PackageTileEntity.NAME),
				TileEntityType.Builder.create(LockerTileEntity::new, EnderMailBlocks.LOCKER).build(null).setRegistryName(LockerTileEntity.NAME)
		);
	}

}
