package com.chaosthedude.endermail.registry;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.entity.EnderMailmanEntity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
@ObjectHolder(EnderMail.MODID)
public class EnderMailEntities {
	
	@ObjectHolder(EnderMailmanEntity.NAME)
	public static final EntityType<EnderMailmanEntity> ENDER_MAILMAN_TYPE = null;
	
	@SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().register(EntityType.Builder.<EnderMailmanEntity>create(EnderMailmanEntity::new, EntityClassification.MONSTER).setTrackingRange(80).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).size(0.6F, 2.9F).setCustomClientFactory((spawnEntity, world) -> new EnderMailmanEntity(ENDER_MAILMAN_TYPE, world)).build(EnderMail.MODID + ":" + EnderMailmanEntity.NAME).setRegistryName(EnderMailmanEntity.NAME));
    }

}
