package com.chaosthedude.endermail.gui.container;

import com.chaosthedude.endermail.blocks.PackageBlock;
import com.chaosthedude.endermail.blocks.te.PackageTileEntity;
import com.chaosthedude.endermail.registry.EnderMailContainers;
import com.chaosthedude.endermail.registry.EnderMailItems;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class PackageContainer extends Container {

	public static final String NAME = "package";

	private final IInventory packageInventory;
	private ITextComponent displayName;

	public PackageContainer(int windowId, PlayerInventory playerInventory) {
		this(windowId, playerInventory, new Inventory(PackageBlock.INVENTORY_SIZE));
	}

	public PackageContainer(int windowId, PlayerInventory playerInventory, IInventory packageInventory) {
		super(EnderMailContainers.PACKAGE_CONTAINER, windowId);
		this.packageInventory = packageInventory;
		displayName = new StringTextComponent(I18n.format("block.endermail.package"));
		if (packageInventory instanceof PackageTileEntity) {
			displayName = ((PackageTileEntity) packageInventory).getDisplayName();
		}
		packageInventory.openInventory(playerInventory.player);
		for (int j = 0; j < packageInventory.getSizeInventory(); ++j) {
			addSlot(new Slot(packageInventory, j, 44 + j * 18, 20) {
				@Override
				public boolean isItemValid(ItemStack stack) {
					return stack.getItem() != EnderMailItems.PACKAGE_ITEM;
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
	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return packageInventory.isUsableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int index) {
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
	public void onContainerClosed(PlayerEntity player) {
		super.onContainerClosed(player);
		packageInventory.closeInventory(player);
	}

	public ITextComponent getDisplayName() {
		return displayName;
	}

}
