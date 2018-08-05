package com.chaosthedude.endermail.blocks.te;

import com.chaosthedude.endermail.blocks.BlockPackage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

public class TileEntityPackage extends TileEntity implements IInventory {

	public static final String NAME = "TileEntityPackage";

	private NonNullList<ItemStack> contents = NonNullList.<ItemStack> withSize(BlockPackage.SIZE, ItemStack.EMPTY);
	public int numPlayersUsing;
	public int deliveryX;
	public int deliveryY;
	public int deliveryZ;
	protected String customName;

	public TileEntityPackage() {
	}
	
	public TileEntityPackage(NonNullList<ItemStack> contents) {
		this.contents = contents;
	}

	@Override
	public int getSizeInventory() {
		return BlockPackage.SIZE;
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
		contents = NonNullList.<ItemStack> withSize(getSizeInventory(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(compound, contents);
		
		deliveryX = compound.getInteger("DeliveryX");
		deliveryY = compound.getInteger("DeliveryY");
		deliveryZ = compound.getInteger("DeliveryZ");
		
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
		
		if (this.hasCustomName()) {
            compound.setString("CustomName", customName);
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
		if (!player.isSpectator()) {
			if (numPlayersUsing < 0) {
				numPlayersUsing = 0;
			}

			numPlayersUsing++;
			world.addBlockEvent(pos, getBlockType(), 1, numPlayersUsing);
			world.notifyNeighborsOfStateChange(pos, getBlockType(), false);
		}
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		if (!player.isSpectator() && getBlockType() instanceof BlockPackage) {
			numPlayersUsing--;
			world.addBlockEvent(pos, getBlockType(), 1, numPlayersUsing);
			world.notifyNeighborsOfStateChange(pos, getBlockType(), false);
		}
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
		ItemStack stack = contents.get(index);
		stack.setCount(stack.getCount() - count);
		return contents.set(index, stack);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack = contents.get(index); // TODO
		contents.set(index, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		contents.set(index, stack);
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
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
		for (int i = 0; i < contents.size(); i++) {
			contents.set(i, ItemStack.EMPTY);
		}
	}

	public NonNullList<ItemStack> getContents() {
		return contents;
	}

	public NBTTagCompound saveToNBT(NBTTagCompound compound) {
		ItemStackHelper.saveAllItems(compound, contents, false);
		if (hasCustomName()) {
			compound.setString("CustomName", customName);
		}

		return compound;
	}

	public void setCustomName(String name) {
		customName = name;
	}
	
	public void setDeliveryPos(BlockPos pos) {
		deliveryX = pos.getX();
		deliveryY = pos.getY();
		deliveryZ = pos.getZ();
	}

}
