package com.chaosthedude.endermail.gui.container;

import com.chaosthedude.endermail.blocks.LockerBlock;
import com.chaosthedude.endermail.registry.EnderMailContainers;
import com.chaosthedude.endermail.registry.EnderMailItems;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class LockerContainer extends Container {

	public static final String NAME = "locker";

	private final IInventory lockerInventory;
	private String lockerID;
	private BlockPos lockerPos;

	public LockerContainer(int windowId, PlayerInventory playerInventory, PacketBuffer packet) {
		this(windowId, playerInventory, new Inventory(LockerBlock.INVENTORY_SIZE), packet.readBlockPos(), packet.readString());
	}

	public LockerContainer(int windowId, PlayerInventory playerInventory, IInventory lockerInventory, BlockPos lockerPos, String lockerID) {
		super(EnderMailContainers.LOCKER_CONTAINER, windowId);
		this.lockerInventory = lockerInventory;
		this.lockerPos = lockerPos;
		this.lockerID = lockerID;
		lockerInventory.openInventory(playerInventory.player);
		for (int j = 0; j < lockerInventory.getSizeInventory(); ++j) {
			addSlot(new Slot(lockerInventory, j, 8 + j * 18, 20) {
				@Override
				public boolean isItemValid(ItemStack stack) {
					return stack.getItem() == EnderMailItems.PACKAGE;
				}
				@Override
				public int getSlotStackLimit() {
					return 1;
				}
			});
		}

		for (int l = 0; l < 3; ++l) {
			for (int k = 0; k < 9; ++k) {
				addSlot(new Slot(playerInventory, k + l * 9 + 9, 8 + k * 18, l * 18 + 51));
			}
		}

		for (int i1 = 0; i1 < 9; ++i1) {
			addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 109));
		}
		detectAndSendChanges();
	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return lockerInventory.isUsableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int index) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = (Slot) inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			stack = slotStack.copy();

			if (index < this.lockerInventory.getSizeInventory()) {
				if (!mergeItemStack(slotStack, lockerInventory.getSizeInventory(), inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!mergeItemStack(slotStack, 0, lockerInventory.getSizeInventory(), false)) {
				return ItemStack.EMPTY;
			}

			if (slotStack.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
		}

		return stack;
	}

	public String getLockerID() {
		return lockerID;
	}
	
	public BlockPos getLockerPos() {
		return lockerPos;
	}

}
