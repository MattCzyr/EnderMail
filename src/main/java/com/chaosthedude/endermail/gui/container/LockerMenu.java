package com.chaosthedude.endermail.gui.container;

import com.chaosthedude.endermail.block.LockerBlock;
import com.chaosthedude.endermail.registry.EnderMailContainers;
import com.chaosthedude.endermail.registry.EnderMailItems;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class LockerMenu extends AbstractContainerMenu {

	public static final String NAME = "locker";

	private final Container lockerContainer;
	private String lockerID;
	private BlockPos lockerPos;

	public LockerMenu(int windowId, Inventory inventory, FriendlyByteBuf buf) {
		this(windowId, inventory, new SimpleContainer(LockerBlock.INVENTORY_SIZE), buf.readBlockPos(), buf.readUtf());
	}

	public LockerMenu(int windowId, Inventory playerInventory, Container lockerContainer, BlockPos lockerPos, String lockerID) {
		super(EnderMailContainers.LOCKER_CONTAINER.get(), windowId);
		this.lockerContainer = lockerContainer;
		this.lockerPos = lockerPos;
		this.lockerID = lockerID;
		lockerContainer.startOpen(playerInventory.player);
		for (int j = 0; j < lockerContainer.getContainerSize(); ++j) {
			addSlot(new Slot(lockerContainer, j, 8 + j * 18, 20) {
				@Override
				public boolean mayPlace(ItemStack stack) {
					return stack.getItem() == EnderMailItems.PACKAGE.get();
				}
				@Override
				public int getMaxStackSize() {
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
		broadcastChanges();
	}

	@Override
	public boolean stillValid(Player player) {
		return lockerContainer.stillValid(player);
	}
	
	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = (Slot) slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			stack = slotStack.copy();

			if (index < lockerContainer.getContainerSize()) {
				if (!moveItemStackTo(slotStack, lockerContainer.getContainerSize(), slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!moveItemStackTo(slotStack, 0, lockerContainer.getContainerSize(), false)) {
				return ItemStack.EMPTY;
			}

			if (slotStack.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
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
