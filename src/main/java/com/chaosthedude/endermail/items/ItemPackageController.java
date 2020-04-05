package com.chaosthedude.endermail.items;

import javax.annotation.Nullable;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.util.EnumControllerState;
import com.chaosthedude.endermail.util.ItemUtils;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPackageController extends Item {

	public static final String NAME = "package_controller";

	public ItemPackageController() {
		super();
		setCreativeTab(CreativeTabs.TOOLS);
		setUnlocalizedName(EnderMail.MODID + "." + NAME);
		addPropertyOverride(new ResourceLocation("state"), new IItemPropertyGetter() {
            @Override
			public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                return getState(stack).getID();
            }
        });
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (player.isSneaking()) {
			setState(player.getHeldItem(hand), EnumControllerState.DEFAULT);
		}
		return super.onItemRightClick(world, player, hand);
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
	
	public void setDeliveryDistance(ItemStack stack, int distance) {
		if (ItemUtils.verifyNBT(stack)) {
			stack.getTagCompound().setInteger("DeliveryDistance", distance);
		}
	}
	
	public void setMaxDistance(ItemStack stack, int distance) {
		if (ItemUtils.verifyNBT(stack)) {
			stack.getTagCompound().setInteger("MaxDistance", distance);
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
	
	public int getDeliveryDistance(ItemStack stack) {
		if (ItemUtils.verifyNBT(stack)) {
			return stack.getTagCompound().getInteger("DeliveryDistance");
		}

		return -1;
	}
	
	public int getMaxDistance(ItemStack stack) {
		if (ItemUtils.verifyNBT(stack)) {
			return stack.getTagCompound().getInteger("MaxDistance");
		}

		return -1;
	}

}
