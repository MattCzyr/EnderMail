package com.chaosthedude.endermail.client;

import com.chaosthedude.endermail.config.ConfigHandler;
import com.chaosthedude.endermail.item.PackageControllerItem;
import com.chaosthedude.endermail.registry.EnderMailItems;
import com.chaosthedude.endermail.util.ControllerState;
import com.chaosthedude.endermail.util.ItemUtils;
import com.chaosthedude.endermail.util.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {

	private static final Minecraft mc = Minecraft.getInstance();

	@SubscribeEvent
	public void onRenderTick(RenderTickEvent event) {
		if (event.phase == Phase.END && mc.player != null && !mc.options.hideGui && !mc.options.renderDebug
				&& (mc.screen == null || (ConfigHandler.CLIENT.displayWithChatOpen.get() && mc.screen instanceof ChatScreen))) {
			final PoseStack poseStack = new PoseStack();
			final Player player = mc.player;
			final ItemStack stack = ItemUtils.getHeldItem(player, EnderMailItems.PACKAGE_CONTROLLER);
			if (stack != null && stack.getItem() instanceof PackageControllerItem) {
				final PackageControllerItem packageController = (PackageControllerItem) stack.getItem();
				if (packageController.getState(stack) == ControllerState.DELIVERING) {
					RenderUtils.drawConfiguredStringOnHUD(poseStack, I18n.get("string.endermail.status"), 5, 0, 0xFFFFFF, 0);
					RenderUtils.drawConfiguredStringOnHUD(poseStack, I18n.get("string.endermail.delivering"), 5, 0, 0xAAAAAA, 1);
				} else if (packageController.getState(stack) == ControllerState.DELIVERED) {
					RenderUtils.drawConfiguredStringOnHUD(poseStack, I18n.get("string.endermail.status"), 5, 0, 0xFFFFFF, 0);
					RenderUtils.drawConfiguredStringOnHUD(poseStack, I18n.get("string.endermail.delivered"), 5, 0, 0xAAAAAA, 1);

					RenderUtils.drawConfiguredStringOnHUD(poseStack, I18n.get("string.endermail.coordinates"), 5, 0, 0xFFFFFF, 3);
					RenderUtils.drawConfiguredStringOnHUD(poseStack, packageController.getDeliveryPos(stack).getX() + " " + packageController.getDeliveryPos(stack).getY() + " " + packageController.getDeliveryPos(stack).getZ(), 5, 0, 0xAAAAAA, 4);
				} else if (packageController.getState(stack) == ControllerState.DELIVERED_TO_LOCKER) {
					RenderUtils.drawConfiguredStringOnHUD(poseStack, I18n.get("string.endermail.status"), 5, 0, 0xFFFFFF, 0);
					RenderUtils.drawConfiguredStringOnHUD(poseStack, I18n.get("string.endermail.deliveredToLocker"), 5, 0, 0xAAAAAA, 1);
					
					RenderUtils.drawConfiguredStringOnHUD(poseStack, I18n.get("string.endermail.lockerID"), 5, 0, 0xFFFFFF, 3);
					RenderUtils.drawConfiguredStringOnHUD(poseStack, packageController.getLockerID(stack), 5, 0, 0xAAAAAA, 4);

					if (packageController.shouldShowLockerLocation(stack)) {
						RenderUtils.drawConfiguredStringOnHUD(poseStack, I18n.get("string.endermail.coordinates"), 5, 0, 0xFFFFFF, 6);
						RenderUtils.drawConfiguredStringOnHUD(poseStack, packageController.getDeliveryPos(stack).getX() + " " + packageController.getDeliveryPos(stack).getY() + " " + packageController.getDeliveryPos(stack).getZ(), 5, 0, 0xAAAAAA, 7);
					}
				} else if (packageController.getState(stack) == ControllerState.UNDELIVERABLE) {
					RenderUtils.drawConfiguredStringOnHUD(poseStack, I18n.get("string.endermail.status"), 5, 0, 0xFFFFFF, 0);
					RenderUtils.drawConfiguredStringOnHUD(poseStack, I18n.get("string.endermail.undeliverable"), 5, 0, 0xAAAAAA, 1);
				} else if (packageController.getState(stack) == ControllerState.TOOFAR) {
					RenderUtils.drawConfiguredStringOnHUD(poseStack, I18n.get("string.endermail.status"), 5, 0, 0xFFFFFF, 0);
					RenderUtils.drawConfiguredStringOnHUD(poseStack, I18n.get("string.endermail.tooFar"), 5, 0, 0xAAAAAA, 1);

					RenderUtils.drawConfiguredStringOnHUD(poseStack, I18n.get("string.endermail.deliveryDistance"), 5, 0, 0xFFFFFF, 3);
					RenderUtils.drawConfiguredStringOnHUD(poseStack, String.valueOf(packageController.getDeliveryDistance(stack)), 5, 0, 0xAAAAAA, 4);

					RenderUtils.drawConfiguredStringOnHUD(poseStack, I18n.get("string.endermail.maxDistance"), 5, 0, 0xFFFFFF, 6);
					RenderUtils.drawConfiguredStringOnHUD(poseStack, String.valueOf(packageController.getMaxDistance(stack)), 5, 0, 0xAAAAAA, 7);
				} else if (packageController.getState(stack) == ControllerState.INVALID_LOCKER) {
					RenderUtils.drawConfiguredStringOnHUD(poseStack, I18n.get("string.endermail.status"), 5, 0, 0xFFFFFF, 0);
					RenderUtils.drawConfiguredStringOnHUD(poseStack, I18n.get("string.endermail.invalidLockerID"), 5, 0, 0xAAAAAA, 1);

					RenderUtils.drawConfiguredStringOnHUD(poseStack, I18n.get("string.endermail.lockerID"), 5, 0, 0xFFFFFF, 3);
					RenderUtils.drawConfiguredStringOnHUD(poseStack, packageController.getLockerID(stack), 5, 0, 0xAAAAAA, 4);
				}
			}
		}
	}

}
