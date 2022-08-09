package com.chaosthedude.endermail.registry;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.entity.EnderMailmanEntity;
import com.google.common.base.Supplier;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EnderMailEntities {
	
	public static final DeferredRegister<EntityType<?>> ENTITY_DEFERRED = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, EnderMail.MODID);

	public static final RegistryObject<EntityType<EnderMailmanEntity>> ENDER_MAILMAN = register(EnderMailmanEntity.NAME, () -> EntityType.Builder.<EnderMailmanEntity>of(EnderMailmanEntity::new, MobCategory.MONSTER).setTrackingRange(80).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).sized(0.6F, 2.9F));

	public static <T extends Entity> RegistryObject<EntityType<T>> register(String name, Supplier<EntityType.Builder<T>> init) {
        return ENTITY_DEFERRED.register(name, () -> init.get().build(EnderMail.MODID + ":" + name));
    }

}
