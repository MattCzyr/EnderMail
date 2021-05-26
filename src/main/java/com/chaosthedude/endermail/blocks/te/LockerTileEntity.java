package com.chaosthedude.endermail.blocks.te;

import com.chaosthedude.endermail.blocks.LockerBlock;
import com.chaosthedude.endermail.data.LockerWorldData;
import com.chaosthedude.endermail.gui.container.LockerContainer;
import com.chaosthedude.endermail.registry.EnderMailBlocks;
import com.chaosthedude.endermail.registry.EnderMailItems;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class LockerTileEntity extends TileEntity implements IInventory, INamedContainerProvider {

	public static final String NAME = "locker";

	private NonNullList<ItemStack> contents = NonNullList.<ItemStack>withSize(LockerBlock.INVENTORY_SIZE, ItemStack.EMPTY);
	protected String lockerID;

	public LockerTileEntity() {
		super(EnderMailBlocks.LOCKER_TE_TYPE);
		lockerID = "";
	}

	public LockerTileEntity(NonNullList<ItemStack> contents) {
		this();
		this.contents = contents;
	}

	@Override
	public int getSizeInventory() {
		return LockerBlock.INVENTORY_SIZE;
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
	public void read(BlockState state, CompoundNBT compound) {
		super.read(state, compound);
		contents = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(compound, contents);

		lockerID = compound.getString("LockerID");
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		ItemStackHelper.saveAllItems(compound, contents);

		compound.putString("LockerID", lockerID);

		return compound;
	}

	public CompoundNBT writeItems(CompoundNBT compound) {
		if (!contents.isEmpty()) {
			ItemStackHelper.saveAllItems(compound, contents);
		}
		return compound;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void openInventory(PlayerEntity player) {
	}

	@Override
	public void closeInventory(PlayerEntity player) {
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return contents.get(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack result = ItemStackHelper.getAndSplit(contents, index, count);
		LockerBlock.setFilled(!isEmpty(), world, pos);
		return result;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack result = ItemStackHelper.getAndRemove(contents, index);
		LockerBlock.setFilled(!isEmpty(), world, pos);
		return result;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		ItemStack itemstack = contents.get(index);
		boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
		contents.set(index, stack);

		if (stack.getCount() > this.getInventoryStackLimit()) {
			stack.setCount(this.getInventoryStackLimit());
		}
		LockerBlock.setFilled(!isEmpty(), world, pos);
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		if (world.getTileEntity(pos) != this) {
			return false;
		} else {
			return player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
		}
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return stack.getItem() == EnderMailItems.PACKAGE;
	}

	@Override
	public void clear() {
		contents.clear();
		LockerBlock.setFilled(!isEmpty(), world, pos);
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
		return new LockerContainer(windowId, playerInventory, this, pos, lockerID);
	}

	@Override
	public ITextComponent getDisplayName() {
		return (ITextComponent) (lockerID != null && !lockerID.isEmpty() ? new StringTextComponent(lockerID) : new TranslationTextComponent("block.endermail.locker"));
	}
	
	@Override
	public void remove() {
		super.remove();
		LockerWorldData data = LockerWorldData.get(world);
		if (data != null) {
			data.removeLocker(lockerID);
		}
	}
	
	public boolean isFull() {
		for (ItemStack stack : contents) {
			if (stack.isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	public boolean addPackage(ItemStack stack) {
		for (int i = 0; i < contents.size(); i++) {
			if (contents.get(i).isEmpty()) {
				setInventorySlotContents(i, stack);
				LockerBlock.setFilled(true, world, pos);
				return true;
			}
		}
		return false;
	}

	public NonNullList<ItemStack> getContents() {
		return contents;
	}
	
	public void setLockerID(String lockerID) {
		LockerWorldData data = LockerWorldData.get(world);
		if (data != null) {
			data.removeLocker(this.lockerID);
			this.lockerID = data.createLocker(lockerID, pos);
		}
	}
	
	public String getLockerID() {
		return lockerID;
	}
	
	public boolean hasLockerID() {
		return lockerID != null && !lockerID.isEmpty();
	}

}
