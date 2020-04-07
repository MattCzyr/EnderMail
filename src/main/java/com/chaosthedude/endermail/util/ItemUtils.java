package com.chaosthedude.endermail.util;

import com.chaosthedude.endermail.registry.EnderMailItems;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class ItemUtils {
	
	public static boolean verifyNBT(ItemStack stack) {
		if (stack.isEmpty() || stack.getItem() != EnderMailItems.PACKAGE_CONTROLLER) {
			return false;
		} else if (!stack.hasTag()) {
			stack.setTag(new CompoundNBT());
		}

		return true;
	}

	public static ItemStack getHeldItem(PlayerEntity player, Item item) {
		if (!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() == item) {
			return player.getHeldItemMainhand();
		} else if (!player.getHeldItemOffhand().isEmpty() && player.getHeldItemOffhand().getItem() == item) {
			return player.getHeldItemOffhand();
		}

		return ItemStack.EMPTY;
	}
	
	public static boolean isHolding(PlayerEntity player, Item item) {
		return player.getHeldItemMainhand().getItem() == item || player.getHeldItemOffhand().getItem() == item;
	}

}
