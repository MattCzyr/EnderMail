package com.chaosthedude.endermail.registry;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.block.LockerBlock;
import com.chaosthedude.endermail.block.PackageBlock;
import com.google.common.base.Supplier;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EnderMailBlocks {
	
	public static final DeferredRegister<Block> BLOCK_DEFERRED = DeferredRegister.create(ForgeRegistries.BLOCKS, EnderMail.MODID);
	
	public static final RegistryObject<PackageBlock> PACKAGE = register(PackageBlock.NAME, () -> new PackageBlock());
	public static final RegistryObject<LockerBlock> LOCKER = register(LockerBlock.NAME, () -> new LockerBlock());

	public static <T extends Block> RegistryObject<T> register(String name, Supplier<T> init) {
        return BLOCK_DEFERRED.register(name, init);
    }

}
