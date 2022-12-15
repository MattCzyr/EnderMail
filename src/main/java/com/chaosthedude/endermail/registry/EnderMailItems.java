package com.chaosthedude.endermail.registry;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.block.LockerBlock;
import com.chaosthedude.endermail.block.PackageBlock;
import com.chaosthedude.endermail.item.PackageControllerItem;
import com.google.common.base.Supplier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EnderMailItems {
	
	public static final DeferredRegister<Item> ITEM_DEFERRED = DeferredRegister.create(ForgeRegistries.ITEMS, EnderMail.MODID);

	public static final RegistryObject<Item> PACKAGE = register(PackageBlock.NAME, () -> new BlockItem(EnderMailBlocks.PACKAGE.get(), new Properties()));
	public static final RegistryObject<Item> LOCKER = register(LockerBlock.NAME, () -> new BlockItem(EnderMailBlocks.LOCKER.get(), new Properties()));
	public static final RegistryObject<Item> PACKAGE_CONTROLLER = register(PackageControllerItem.NAME, () -> new PackageControllerItem());
	public static final RegistryObject<Item> PACKING_TAPE = register("packing_tape", () -> new Item(new Properties()));
	public static final RegistryObject<Item> STAMP = register("stamp", () -> new Item(new Properties()));

	public static RegistryObject<Item> register(String name, Supplier<Item> init) {
        return ITEM_DEFERRED.register(name, init);
    }

}
