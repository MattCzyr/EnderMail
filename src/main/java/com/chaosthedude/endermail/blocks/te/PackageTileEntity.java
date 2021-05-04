package com.chaosthedude.endermail.blocks.te;

import com.chaosthedude.endermail.blocks.PackageBlock;
import com.chaosthedude.endermail.gui.container.PackageContainer;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class PackageTileEntity extends TileEntity implements IInventory, INamedContainerProvider {

	public static final String NAME = "package";

	private NonNullList<ItemStack> contents = NonNullList.<ItemStack>withSize(PackageBlock.INVENTORY_SIZE, ItemStack.EMPTY);
	private int deliveryX;
	private int deliveryY;
	private int deliveryZ;
	private String lockerID;
	private boolean hasDeliveryLocation;
	private boolean hasLockerID;
	protected ITextComponent customName;

	public PackageTileEntity() {
		super(EnderMailBlocks.PACKAGE_TE_TYPE);
		deliveryX = -1;
		deliveryY = -1;
		deliveryZ = -1;
		lockerID = "";
		hasDeliveryLocation = false;
	}

	public PackageTileEntity(NonNullList<ItemStack> contents) {
		this();
		this.contents = contents;
	}

	@Override
	public int getSizeInventory() {
		return PackageBlock.INVENTORY_SIZE;
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

		deliveryX = compound.getInt("DeliveryX");
		deliveryY = compound.getInt("DeliveryY");
		deliveryZ = compound.getInt("DeliveryZ");
		
		lockerID = compound.getString("LockerID");

		hasDeliveryLocation = compound.getBoolean("HasDeliveryLocation");
		hasLockerID = compound.getBoolean("HasLockerID");

		if (compound.contains("CustomName", 8)) {
			customName = ITextComponent.Serializer.getComponentFromJson(compound.getString("CustomName"));
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		ItemStackHelper.saveAllItems(compound, contents);

		compound.putInt("DeliveryX", deliveryX);
		compound.putInt("DeliveryY", deliveryY);
		compound.putInt("DeliveryZ", deliveryZ);
		
		compound.putString("LockerID", lockerID);

		compound.putBoolean("HasDeliveryLocation", hasDeliveryLocation);
		compound.putBoolean("HasLockerID", hasLockerID);

		if (hasCustomName()) {
			compound.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
		}

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
		return ItemStackHelper.getAndSplit(contents, index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStackHelper.getAndRemove(contents, index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		ItemStack itemstack = contents.get(index);
		boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
		contents.set(index, stack);

		if (stack.getCount() > this.getInventoryStackLimit()) {
			stack.setCount(this.getInventoryStackLimit());
		}
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
		return stack.getItem() != EnderMailItems.PACKAGE;
	}

	@Override
	public void clear() {
		contents.clear();
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
		return new PackageContainer(windowId, playerInventory, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return (ITextComponent) (customName != null ? customName : new TranslationTextComponent("block.endermail.package"));
	}

	public NonNullList<ItemStack> getContents() {
		return contents;
	}

	public void setDeliveryPos(BlockPos pos, boolean hasDeliveryLocation) {
		this.hasDeliveryLocation = hasDeliveryLocation;
		deliveryX = pos.getX();
		deliveryY = pos.getY();
		deliveryZ = pos.getZ();
	}
	
	public void setLockerID(String lockerID) {
		hasLockerID = true;
		this.lockerID = lockerID;
	}
	
	public String getLockerID() {
		if (hasLockerID) {
			return lockerID;
		}
		return null;
	}

	public BlockPos getDeliveryPos() {
		return new BlockPos(deliveryX, deliveryY, deliveryZ);
	}
	
	public boolean hasDeliveryLocation() {
		return hasDeliveryLocation;
	}

	public void setCustomName(ITextComponent name) {
		customName = name;
	}

	public boolean hasCustomName() {
		return customName != null && !customName.getString().isEmpty();
	}

}
