package com.chaosthedude.endermail.registry;

import com.chaosthedude.endermail.client.render.EnderMailmanRenderer;
import com.chaosthedude.endermail.client.render.model.EnderMailmanModel;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class EnderMailRenderers {

	@SubscribeEvent
	public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(EnderMailEntities.ENDER_MAILMAN.get(), (context) -> new EnderMailmanRenderer(context));
	}

	@SubscribeEvent
	public static void registerLayers(final EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(EnderMailmanModel.LOCATION, () -> EnderMailmanModel.createBodyLayer());
	}

}
