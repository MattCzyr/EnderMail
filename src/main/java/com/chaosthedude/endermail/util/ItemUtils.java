package com.chaosthedude.endermail.util;

import com.chaosthedude.endermail.registry.EnderMailItems;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemUtils {

	public static boolean verifyNBT(ItemStack stack) {
		if (stack.isEmpty() || stack.getItem() != EnderMailItems.PACKAGE_CONTROLLER) {
			return false;
		} else if (!stack.hasTag()) {
			stack.setTag(new CompoundTag());
		}

		return true;
	}

	public static ItemStack getHeldItem(Player player, Item item) {
		if (!player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem() == item) {
			return player.getMainHandItem();
		} else if (!player.getOffhandItem().isEmpty() && player.getOffhandItem().getItem() == item) {
			return player.getOffhandItem();
		}

		return ItemStack.EMPTY;
	}

	public static boolean isHolding(Player player, Item item) {
		return player.getMainHandItem().getItem() == item || player.getOffhandItem().getItem() == item;
	}

}
