package com.chaosthedude.endermail.items;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.util.EnumControllerState;
import com.chaosthedude.endermail.util.ItemUtils;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ItemPackageController extends Item {

	public static final String NAME = "package_controller";

	public ItemPackageController() {
		super();
		setCreativeTab(CreativeTabs.TOOLS);
		setUnlocalizedName(EnderMail.MODID + "." + NAME);
	}
	
	public void setState(ItemStack stack, EnumControllerState state) {
		if (ItemUtils.verifyNBT(stack)) {
			stack.getTagCompound().setInteger("State", state.getID());
		}
	}
	
	public void setDeliveryPos(ItemStack stack, BlockPos pos) {
		if (ItemUtils.verifyNBT(stack)) {
			stack.getTagCompound().setInteger("DeliveryX", pos.getX());
			stack.getTagCompound().setInteger("DeliveryY", pos.getY());
			stack.getTagCompound().setInteger("DeliveryZ", pos.getZ());
		}
	}
	
	public EnumControllerState getState(ItemStack stack) {
		if (ItemUtils.verifyNBT(stack)) {
			return EnumControllerState.fromID(stack.getTagCompound().getInteger("State"));
		}

		return null;
	}
	
	public BlockPos getDeliveryPos(ItemStack stack) {
		if (ItemUtils.verifyNBT(stack)) {
			return new BlockPos(stack.getTagCompound().getInteger("DeliveryX"), stack.getTagCompound().getInteger("DeliveryY"), stack.getTagCompound().getInteger("DeliveryZ"));
		}

		return null;
	}

}
