package com.chaosthedude.endermail.items;

import javax.annotation.Nullable;

import com.chaosthedude.endermail.util.ControllerState;
import com.chaosthedude.endermail.util.ItemUtils;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PackageControllerItem extends Item {

	public static final String NAME = "package_controller";

	public PackageControllerItem() {
		super(new Properties().group(ItemGroup.TOOLS));
		addPropertyOverride(new ResourceLocation("state"), new IItemPropertyGetter() {
			@Override
			public float call(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
				return getState(stack).getID();
			}
		});
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		if (player.isCrouching()) {
			setState(player.getHeldItem(hand), ControllerState.DEFAULT);
		}
		return super.onItemRightClick(world, player, hand);
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

}
