package com.chaosthedude.endermail.registry;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.gui.container.LockerMenu;
import com.chaosthedude.endermail.gui.container.PackageMenu;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
@ObjectHolder(EnderMail.MODID)
public class EnderMailContainers {

	@ObjectHolder(PackageMenu.NAME)
	public static final MenuType<PackageMenu> PACKAGE_CONTAINER = null;
	
	@ObjectHolder(LockerMenu.NAME)
	public static final MenuType<LockerMenu> LOCKER_CONTAINER = null;

	@SubscribeEvent
	public static void onContainerTypeRegistry(final RegistryEvent.Register<MenuType<?>> event) {
		event.getRegistry().registerAll(
				IForgeContainerType.create(LockerMenu::new).setRegistryName(LockerMenu.NAME),
				new MenuType<>(PackageMenu::new).setRegistryName(PackageMenu.NAME)
		);
	}

}
