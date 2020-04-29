package com.chaosthedude.endermail.registry;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.gui.container.LockerContainer;
import com.chaosthedude.endermail.gui.container.PackageContainer;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
@ObjectHolder(EnderMail.MODID)
public class EnderMailContainers {

	@ObjectHolder(PackageContainer.NAME)
	public static final ContainerType<PackageContainer> PACKAGE_CONTAINER = null;
	
	@ObjectHolder(LockerContainer.NAME)
	public static final ContainerType<LockerContainer> LOCKER_CONTAINER = null;

	@SubscribeEvent
	public static void onContainerTypeRegistry(final RegistryEvent.Register<ContainerType<?>> event) {
		event.getRegistry().registerAll(
				IForgeContainerType.create(LockerContainer::new).setRegistryName(LockerContainer.NAME),
				new ContainerType<>(PackageContainer::new).setRegistryName(PackageContainer.NAME)
				//new ContainerType<>(LockerContainer::new).setRegistryName(LockerContainer.NAME)
		);
	}

}
