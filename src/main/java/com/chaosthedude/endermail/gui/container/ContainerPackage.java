package com.chaosthedude.endermail.gui.container;

import com.chaosthedude.endermail.blocks.te.TileEntityPackage;
import com.chaosthedude.endermail.registry.EnderMailBlocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ContainerPackage extends Container {

	private final IInventory packageInventory;

	public ContainerPackage(IInventory playerInventory, IInventory packageInventory, EntityPlayer player) {
		this.packageInventory = packageInventory;
		packageInventory.openInventory(player);
		int i = 51;

		for (int j = 0; j < packageInventory.getSizeInventory(); ++j) {
			addSlotToContainer(new Slot(packageInventory, j, 44 + j * 18, 20) {
				@Override
				public boolean isItemValid(ItemStack stack) {
					return stack.getItem() != Item.getItemFromBlock(EnderMailBlocks.default_package) && stack.getItem() != Item.getItemFromBlock(EnderMailBlocks.stamped_package);
				}
			});
		}

		for (int l = 0; l < 3; ++l) {
			for (int k = 0; k < 9; ++k) {
				addSlotToContainer(new Slot(playerInventory, k + l * 9 + 9, 8 + k * 18, l * 18 + 51));
			}
		}

		for (int i1 = 0; i1 < 9; ++i1) {
			addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 109));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return packageInventory.isUsableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = (Slot) inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			stack = slotStack.copy();

			if (index < this.packageInventory.getSizeInventory()) {
				if (!mergeItemStack(slotStack, packageInventory.getSizeInventory(), inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!mergeItemStack(slotStack, 0, packageInventory.getSizeInventory(), false)) {
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

	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		packageInventory.closeInventory(player);
	}

	public IInventory getPackageInventory() {
		return packageInventory;
	}

}
