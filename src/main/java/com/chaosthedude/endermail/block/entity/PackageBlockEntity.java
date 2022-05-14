package com.chaosthedude.endermail.block.entity;

import com.chaosthedude.endermail.block.PackageBlock;
import com.chaosthedude.endermail.gui.container.PackageMenu;
import com.chaosthedude.endermail.registry.EnderMailBlocks;
import com.chaosthedude.endermail.registry.EnderMailItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PackageBlockEntity extends BaseContainerBlockEntity {

	public static final String NAME = "package";

	private NonNullList<ItemStack> contents = NonNullList.<ItemStack>withSize(PackageBlock.INVENTORY_SIZE, ItemStack.EMPTY);
	private int deliveryX;
	private int deliveryY;
	private int deliveryZ;
	private String lockerID;
	private boolean hasDeliveryLocation;
	private boolean hasLockerID;

	public PackageBlockEntity(BlockPos pos, BlockState state) {
		super(EnderMailBlocks.PACKAGE_TE_TYPE, pos, state);
		deliveryX = -1;
		deliveryY = -1;
		deliveryZ = -1;
		lockerID = "";
		hasDeliveryLocation = false;
	}

	public PackageBlockEntity(NonNullList<ItemStack> contents, BlockPos pos, BlockState state) {
		this(pos, state);
		this.contents = contents;
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
	public void load(CompoundTag tag) {
		super.load(tag);
		contents = NonNullList.<ItemStack>withSize(getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, contents);

		deliveryX = tag.getInt("DeliveryX");
		deliveryY = tag.getInt("DeliveryY");
		deliveryZ = tag.getInt("DeliveryZ");
		
		lockerID = tag.getString("LockerID");

		hasDeliveryLocation = tag.getBoolean("HasDeliveryLocation");
		hasLockerID = tag.getBoolean("HasLockerID");
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		super.save(tag);
		ContainerHelper.saveAllItems(tag, contents);

		tag.putInt("DeliveryX", deliveryX);
		tag.putInt("DeliveryY", deliveryY);
		tag.putInt("DeliveryZ", deliveryZ);
		
		tag.putString("LockerID", lockerID);

		tag.putBoolean("HasDeliveryLocation", hasDeliveryLocation);
		tag.putBoolean("HasLockerID", hasLockerID);

		return tag;
	}

	public CompoundTag writeItems(CompoundTag tag) {
		if (!contents.isEmpty()) {
			ContainerHelper.saveAllItems(tag, contents);
		}
		return tag;
	}
	
	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public void startOpen(Player player) {
	}

	@Override
	public void stopOpen(Player player) {
	}

	@Override
	public ItemStack getItem(int index) {
		return contents.get(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		return ContainerHelper.removeItem(contents, index, count);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return ContainerHelper.takeItem(contents, index);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		ItemStack itemstack = contents.get(index);
		boolean flag = !stack.isEmpty() && ItemStack.isSameItemSameTags(stack, itemstack);
		contents.set(index, stack);

		if (stack.getCount() > getMaxStackSize()) {
			stack.setCount(getMaxStackSize());
		}
	}

	@Override
	public boolean stillValid(Player player) {
		if (level.getBlockEntity(worldPosition) != this) {
			return false;
		} else {
			return player.distanceToSqr((double) worldPosition.getX() + 0.5D, (double) worldPosition.getY() + 0.5D, (double) worldPosition.getZ() + 0.5D) <= 64.0D;
		}
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return stack.getItem() != EnderMailItems.PACKAGE;
	}
	
	@Override
	public void clearContent() {
		contents.clear();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory) {
		return new PackageMenu(windowId, playerInventory, this);
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

	@Override
	public int getContainerSize() {
		return PackageBlock.INVENTORY_SIZE;
	}

	@Override
	protected Component getDefaultName() {
		return new TranslatableComponent("block.endermail.package");
	}

}
