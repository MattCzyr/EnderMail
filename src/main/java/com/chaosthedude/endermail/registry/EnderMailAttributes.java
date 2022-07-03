package com.chaosthedude.endermail.registry;

import com.chaosthedude.endermail.entity.EnderMailmanEntity;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class EnderMailAttributes {
	
	@SubscribeEvent
	public static void registerAttributes(final EntityAttributeCreationEvent event) {
		event.put(EnderMailEntities.ENDER_MAILMAN.get(), EnderMailmanEntity.createAttributes().build());
	}

}
