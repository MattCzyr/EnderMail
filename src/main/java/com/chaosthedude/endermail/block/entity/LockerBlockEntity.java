package com.chaosthedude.endermail.block.entity;

import com.chaosthedude.endermail.block.LockerBlock;
import com.chaosthedude.endermail.data.LockerWorldData;
import com.chaosthedude.endermail.gui.container.LockerMenu;
import com.chaosthedude.endermail.registry.EnderMailBlocks;
import com.chaosthedude.endermail.registry.EnderMailItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LockerBlockEntity extends BaseContainerBlockEntity {

	public static final String NAME = "locker";

	private NonNullList<ItemStack> contents = NonNullList.<ItemStack>withSize(LockerBlock.INVENTORY_SIZE, ItemStack.EMPTY);
	protected String lockerID;

	public LockerBlockEntity(BlockPos pos, BlockState state) {
		super(EnderMailBlocks.LOCKER_TE_TYPE, pos, state);
		lockerID = "";
	}

	public LockerBlockEntity(NonNullList<ItemStack> contents, BlockPos pos, BlockState state) {
		this(pos, state);
		this.contents = contents;
	}

	@Override
	public int getContainerSize() {
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
	public void load(CompoundTag tag) {
		super.load(tag);
		contents = NonNullList.<ItemStack>withSize(getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, contents);
		lockerID = tag.getString("LockerID");
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		ContainerHelper.saveAllItems(tag, contents);
		tag.putString("LockerID", lockerID);
	}

	public CompoundTag writeItems(CompoundTag compound) {
		if (!contents.isEmpty()) {
			ContainerHelper.saveAllItems(compound, contents);
		}
		return compound;
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public ItemStack getItem(int index) {
		return contents.get(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack result = ContainerHelper.removeItem(contents, index, count);
		LockerBlock.setFilled(!isEmpty(), level, worldPosition);
		return result;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		ItemStack result = ContainerHelper.takeItem(contents, index);
		LockerBlock.setFilled(!isEmpty(), level, worldPosition);
		return result;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		ItemStack itemstack = contents.get(index);
		boolean flag = !stack.isEmpty() && ItemStack.isSameItemSameTags(stack, itemstack);
		contents.set(index, stack);

		if (stack.getCount() > getMaxStackSize()) {
			stack.setCount(getMaxStackSize());
		}
		LockerBlock.setFilled(!isEmpty(), level, worldPosition);
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
		return stack.getItem() == EnderMailItems.PACKAGE;
	}

	@Override
	public void clearContent() {
		contents.clear();
		LockerBlock.setFilled(!isEmpty(), level, worldPosition);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowID, Inventory playerInventory) {
		return new LockerMenu(windowID, playerInventory, this, worldPosition, lockerID);
	}

	@Override
	public Component getDisplayName() {
		return lockerID != null && !lockerID.isEmpty() ? new TextComponent(lockerID) : new TranslatableComponent("block.endermail.locker");
	}

	@Override
	public void setRemoved() {
		if (level instanceof ServerLevel) {
			ServerLevel serverLevel = (ServerLevel) level;
			LockerWorldData data = LockerWorldData.get(serverLevel);
			if (data != null) {
				data.removeLocker(lockerID);
			}
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
				setItem(i, stack);
				LockerBlock.setFilled(true, level, worldPosition);
				return true;
			}
		}
		return false;
	}

	public NonNullList<ItemStack> getContents() {
		return contents;
	}

	public void setLockerID(String lockerID) {
		if (level instanceof ServerLevel) {
			ServerLevel serverLevel = (ServerLevel) level;
			LockerWorldData data = LockerWorldData.get(serverLevel);
			if (data != null) {
				data.removeLocker(this.lockerID);
				this.lockerID = data.createLocker(lockerID, worldPosition);
			}
		}
	}

	public String getLockerID() {
		return lockerID;
	}

	public boolean hasLockerID() {
		return lockerID != null && !lockerID.isEmpty();
	}

	@Override
	protected Component getDefaultName() {
		return new TranslatableComponent("block.endermail.locker");
	}

}
