package com.chaosthedude.endermail.item;

import com.chaosthedude.endermail.util.ControllerState;
import com.chaosthedude.endermail.util.ItemUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PackageControllerItem extends Item {

	public static final String NAME = "package_controller";

	public PackageControllerItem() {
		super(new Properties().tab(CreativeModeTab.TAB_TOOLS));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if (player.isCrouching()) {
			setState(player.getItemInHand(hand), ControllerState.DEFAULT);
		}
		return super.use(level, player, hand);
	}

	public void setState(ItemStack stack, ControllerState state) {
		if (ItemUtils.verifyNBT(stack)) {
			stack.getTag().putInt("State", state.getID());
		}
	}

	public void setDeliveryPos(ItemStack stack, BlockPos pos) {
		if (ItemUtils.verifyNBT(stack)) {
			stack.getTag().putInt("DeliveryX", pos.getX());
			stack.getTag().putInt("DeliveryY", pos.getY());
			stack.getTag().putInt("DeliveryZ", pos.getZ());
		}
	}

	public void setDeliveryDistance(ItemStack stack, int distance) {
		if (ItemUtils.verifyNBT(stack)) {
			stack.getTag().putInt("DeliveryDistance", distance);
		}
	}

	public void setMaxDistance(ItemStack stack, int distance) {
		if (ItemUtils.verifyNBT(stack)) {
			stack.getTag().putInt("MaxDistance", distance);
		}
	}
	
	public void setLockerID(ItemStack stack, String id) {
		if (ItemUtils.verifyNBT(stack)) {
			stack.getTag().putString("LockerID", id);
		}
	}
	
	public void setShowLockerLocation(ItemStack stack, boolean show) {
		if (ItemUtils.verifyNBT(stack)) {
			stack.getTag().putBoolean("ShowLockerLocation", show);
		}
	}

	public ControllerState getState(ItemStack stack) {
		if (ItemUtils.verifyNBT(stack)) {
			return ControllerState.fromID(stack.getTag().getInt("State"));
		}

		return null;
	}

	public BlockPos getDeliveryPos(ItemStack stack) {
		if (ItemUtils.verifyNBT(stack)) {
			return new BlockPos(stack.getTag().getInt("DeliveryX"), stack.getTag().getInt("DeliveryY"), stack.getTag().getInt("DeliveryZ"));
		}

		return null;
	}

	public int getDeliveryDistance(ItemStack stack) {
		if (ItemUtils.verifyNBT(stack)) {
			return stack.getTag().getInt("DeliveryDistance");
		}

		return -1;
	}

	public int getMaxDistance(ItemStack stack) {
		if (ItemUtils.verifyNBT(stack)) {
			return stack.getTag().getInt("MaxDistance");
		}

		return -1;
	}
	
	public String getLockerID(ItemStack stack) {
		if (ItemUtils.verifyNBT(stack)) {
			return stack.getTag().getString("LockerID");
		}
		return "";
	}
	
	public boolean shouldShowLockerLocation(ItemStack stack) {
		if (ItemUtils.verifyNBT(stack)) {
			return stack.getTag().getBoolean("ShowLockerLocation");
		}
		return false;
	}

}
