package com.chaosthedude.endermail.blocks.te;

import com.chaosthedude.endermail.blocks.BlockPackage;
import com.chaosthedude.endermail.registry.EnderMailBlocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

public class TileEntityPackage extends TileEntity implements IInventory {

	public static final String NAME = "TileEntityPackage";

	private NonNullList<ItemStack> contents = NonNullList.<ItemStack>withSize(BlockPackage.INVENTORY_SIZE, ItemStack.EMPTY);
	public int numPlayersUsing;
	private int deliveryX;
	private int deliveryY;
	private int deliveryZ;
	private boolean hasDeliveryLocation;
	protected String customName;

	public TileEntityPackage() {
		deliveryX = -1;
		deliveryY = -1;
		deliveryZ = -1;
	}

	public TileEntityPackage(NonNullList<ItemStack> contents) {
		this.contents = contents;
	}

	@Override
	public int getSizeInventory() {
		return BlockPackage.INVENTORY_SIZE;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : contents) {
			if (!stack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String getName() {
		return "tile.endermail.package.name";
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		contents = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(compound, contents);

		deliveryX = compound.getInteger("DeliveryX");
		deliveryY = compound.getInteger("DeliveryY");
		deliveryZ = compound.getInteger("DeliveryZ");
		
		hasDeliveryLocation = compound.getBoolean("HasDeliveryLocation");

		if (compound.hasKey("CustomName", 8)) {
			customName = compound.getString("CustomName");
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		ItemStackHelper.saveAllItems(compound, contents);

		compound.setInteger("DeliveryX", deliveryX);
		compound.setInteger("DeliveryY", deliveryY);
		compound.setInteger("DeliveryZ", deliveryZ);
		
		compound.setBoolean("HasDeliveryLocation", hasDeliveryLocation);

		if (this.hasCustomName()) {
			compound.setString("CustomName", customName);
		}

		return compound;
	}
	
	public NBTTagCompound writeItems(NBTTagCompound compound) {
		if (!contents.isEmpty()) {
			ItemStackHelper.saveAllItems(compound, contents, false);
		}
		return compound;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean receiveClientEvent(int id, int type) {
		if (id == 1) {
			numPlayersUsing = type;
			return true;
		} else {
			return super.receiveClientEvent(id, type);
		}
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public boolean hasCustomName() {
		return customName != null && !customName.isEmpty();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return contents.get(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return ItemStackHelper.getAndSplit(contents, index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStackHelper.getAndRemove(contents, index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		ItemStack itemstack = contents.get(index);
		boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack)
				&& ItemStack.areItemStackTagsEqual(stack, itemstack);
		contents.set(index, stack);

		if (stack.getCount() > this.getInventoryStackLimit()) {
			stack.setCount(this.getInventoryStackLimit());
		}
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		if (world.getTileEntity(pos) != this) {
			return false;
		} else {
			return player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D,
					(double) pos.getZ() + 0.5D) <= 64.0D;
		}
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return stack.getItem() != Item.getItemFromBlock(EnderMailBlocks.package_block);
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		contents.clear();
	}

	public NonNullList<ItemStack> getContents() {
		return contents;
	}

	public BlockPos getDeliveryPos() {
		if (hasDeliveryLocation) {
			return new BlockPos(deliveryX, deliveryY, deliveryZ);
		}

		return null;
	}

	public void setDeliveryPos(BlockPos pos) {
		hasDeliveryLocation = true;
		deliveryX = pos.getX();
		deliveryY = pos.getY();
		deliveryZ = pos.getZ();
	}
	
	public void setCustomName(String name) {
		customName = name;
	}
	
	public String getCustomName() {
		return customName;
	}

}
