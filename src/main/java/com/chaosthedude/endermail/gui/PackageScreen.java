package com.chaosthedude.endermail.gui;

import com.chaosthedude.endermail.gui.container.PackageContainer;
import com.chaosthedude.endermail.util.RenderUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PackageScreen extends ContainerScreen<PackageContainer> implements IHasContainer<PackageContainer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("endermail:textures/gui/package.png");

	private final PackageContainer containerPackage;
	private final PlayerInventory playerInventory;

	public PackageScreen(PackageContainer containerPackage, PlayerInventory playerInventory, ITextComponent title) {
		super(containerPackage, playerInventory, title);
		this.playerInventory = playerInventory;
		this.containerPackage = containerPackage;
		ySize = 133;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderHoveredTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
		font.func_243248_b(matrixStack, containerPackage.getDisplayName(), 8, 6, 4210752);
		font.func_243248_b(matrixStack, playerInventory.getDisplayName(), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		RenderUtils.drawTexturedModalRect(i, j, 0, 0, xSize, ySize);
	}

}
