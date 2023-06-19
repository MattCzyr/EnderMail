package com.chaosthedude.endermail.gui;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.gui.container.PackageMenu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PackageScreen extends AbstractContainerScreen<PackageMenu> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(EnderMail.MODID, "textures/gui/package.png");

	public PackageScreen(PackageMenu containerPackage, Inventory playerInventory, Component title) {
		super(containerPackage, playerInventory, title);
		imageHeight = 133;
		inventoryLabelY = imageHeight - 94;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		int i = (width - imageWidth) / 2;
		int j = (height - imageHeight) / 2;
		guiGraphics.blit(TEXTURE, i, j, 0, 0, imageWidth, imageHeight);
	}

}
