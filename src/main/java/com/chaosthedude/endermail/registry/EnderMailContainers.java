package com.chaosthedude.endermail.registry;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.gui.container.LockerMenu;
import com.chaosthedude.endermail.gui.container.PackageMenu;
import com.google.common.base.Supplier;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EnderMailContainers {
	
	public static final DeferredRegister<MenuType<?>> CONTAINER_DEFERRED = DeferredRegister.create(ForgeRegistries.CONTAINERS, EnderMail.MODID);
	
	public static final RegistryObject<MenuType<PackageMenu>> PACKAGE_CONTAINER = register(PackageMenu.NAME, () -> new MenuType<>(PackageMenu::new));
	public static final RegistryObject<MenuType<LockerMenu>> LOCKER_CONTAINER = register(LockerMenu.NAME, () -> IForgeMenuType.create(LockerMenu::new));

	public static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String name, Supplier<MenuType<T>> init) {
        return CONTAINER_DEFERRED.register(name, init);
    }

}
