package com.chaosthedude.endermail.gui.container;

import com.chaosthedude.endermail.block.PackageBlock;
import com.chaosthedude.endermail.config.ConfigHandler;
import com.chaosthedude.endermail.registry.EnderMailContainers;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class PackageMenu extends AbstractContainerMenu {

	public static final String NAME = "package";

	private final Container packageContainer;

	public PackageMenu(int windowId, Inventory playerInventory) {
		this(windowId, playerInventory, new SimpleContainer(PackageBlock.INVENTORY_SIZE));
	}

	public PackageMenu(int windowId, Inventory playerInventory, Container packageContainer) {
		super(EnderMailContainers.PACKAGE_CONTAINER.get(), windowId);
		this.packageContainer = packageContainer;
		packageContainer.startOpen(playerInventory.player);
		for (int j = 0; j < packageContainer.getContainerSize(); ++j) {
			addSlot(new Slot(packageContainer, j, 44 + j * 18, 20) {
				@Override
				public boolean mayPlace(ItemStack stack) {
					return !ConfigHandler.GENERAL.packageContentsBlacklist.get().contains(ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
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
	public boolean stillValid(Player player) {
		return packageContainer.stillValid(player);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = (Slot) slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			stack = slotStack.copy();

			if (index < packageContainer.getContainerSize()) {
				if (!moveItemStackTo(slotStack, packageContainer.getContainerSize(), slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!moveItemStackTo(slotStack, 0, packageContainer.getContainerSize(), false)) {
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

	@Override
	public void removed(Player player) {
		super.removed(player);
		packageContainer.stopOpen(player);
	}

	//@Override
	//public ITextComponent getName() {
	//	return new StringTextComponent(I18n.format("block.endermail.package"));
	//}

}
