package com.chaosthedude.endermail.gui;

import org.lwjgl.opengl.GL11;

import com.chaosthedude.endermail.blocks.te.TileEntityPackage;
import com.chaosthedude.endermail.gui.container.ContainerPackage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiPackage extends GuiContainer {

	private static final ResourceLocation TEXTURE = new ResourceLocation("endermail:textures/gui/package.png");
	private final IInventory playerInventory;
	private final IInventory packageInventory;

	public GuiPackage(InventoryPlayer playerInventory, IInventory packageInventory) {
		super(new ContainerPackage(playerInventory, packageInventory, Minecraft.getMinecraft().player));
		this.playerInventory = playerInventory;
		this.packageInventory = packageInventory;
		allowUserInput = false;
		ySize = 133;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format(packageInventory.getName()), 8, 6, 4210752);
		fontRenderer.drawString(playerInventory.getDisplayName().getUnformattedText(), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		drawTexturedModalRect(i, j, 0, 0, xSize, ySize);
	}

}
