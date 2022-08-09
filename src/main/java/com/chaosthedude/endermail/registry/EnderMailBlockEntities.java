package com.chaosthedude.endermail.registry;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.block.LockerBlock;
import com.chaosthedude.endermail.block.PackageBlock;
import com.chaosthedude.endermail.block.entity.LockerBlockEntity;
import com.chaosthedude.endermail.block.entity.PackageBlockEntity;
import com.google.common.base.Supplier;
import com.mojang.datafixers.types.Type;

import net.minecraft.Util;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EnderMailBlockEntities {
	
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_DEFERRED = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, EnderMail.MODID);
	
	public static final RegistryObject<BlockEntityType<PackageBlockEntity>> PACKAGE = register(PackageBlock.NAME, () -> BlockEntityType.Builder.of(PackageBlockEntity::new, EnderMailBlocks.PACKAGE.get()));
	public static final RegistryObject<BlockEntityType<LockerBlockEntity>> LOCKER = register(LockerBlock.NAME, () -> BlockEntityType.Builder.of(LockerBlockEntity::new, EnderMailBlocks.LOCKER.get()));

	public static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, Supplier<BlockEntityType.Builder<T>> initializer) {
        Type<?> type = Util.fetchChoiceType(References.BLOCK_ENTITY, EnderMail.MODID + ":" + name);
        return BLOCK_ENTITY_DEFERRED.register(name, () -> initializer.get().build(type));
    }

}
