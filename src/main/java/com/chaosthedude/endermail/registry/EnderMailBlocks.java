package com.chaosthedude.endermail.registry;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.block.LockerBlock;
import com.chaosthedude.endermail.block.PackageBlock;
import com.chaosthedude.endermail.block.entity.LockerBlockEntity;
import com.chaosthedude.endermail.block.entity.PackageBlockEntity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
@ObjectHolder(EnderMail.MODID)
public class EnderMailBlocks {

	@ObjectHolder(PackageBlock.NAME)
	public static final PackageBlock PACKAGE = null;

	@ObjectHolder(PackageBlockEntity.NAME)
	public static final BlockEntityType<?> PACKAGE_TE_TYPE = null;
	
	@ObjectHolder(LockerBlock.NAME)
	public static final LockerBlock LOCKER = null;
	
	@ObjectHolder(LockerBlockEntity.NAME)
	public static final BlockEntityType<?> LOCKER_TE_TYPE = null;

	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(
				new PackageBlock().setRegistryName(PackageBlock.NAME),
				new LockerBlock().setRegistryName(LockerBlock.NAME)
		);
	}

	@SubscribeEvent
	public static void registerTileEntities(final RegistryEvent.Register<BlockEntityType<?>> event) {
		event.getRegistry().registerAll(
				BlockEntityType.Builder.of(PackageBlockEntity::new, EnderMailBlocks.PACKAGE).build(null).setRegistryName(PackageBlockEntity.NAME),
				BlockEntityType.Builder.of(LockerBlockEntity::new, EnderMailBlocks.LOCKER).build(null).setRegistryName(LockerBlockEntity.NAME)
		);
	}

}
