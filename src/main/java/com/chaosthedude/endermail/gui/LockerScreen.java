package com.chaosthedude.endermail.gui;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.block.LockerBlock;
import com.chaosthedude.endermail.gui.container.LockerMenu;
import com.chaosthedude.endermail.network.ConfigureLockerPacket;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LockerScreen extends AbstractContainerScreen<LockerMenu> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(EnderMail.MODID, "textures/gui/locker.png");

	private EditBox idTextField;

	private BlockPos lockerPos;
	private String lockerID;

	public LockerScreen(LockerMenu containerLocker, Inventory playerInventory, Component title) {
		super(containerLocker, playerInventory, title);
		lockerPos = containerLocker.getLockerPos();
		lockerID = containerLocker.getLockerID();
		imageHeight = 133;
		inventoryLabelY = imageHeight - 94;
	}

	@Override
	protected void init() {
		super.init();
		setupTextFields();
	}

	@Override
	public void containerTick() {
		super.containerTick();
		idTextField.tick();
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		idTextField.render(guiGraphics, mouseX, mouseY, partialTicks);
		renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int par2, int par3) {
		super.renderLabels(guiGraphics, par2, par3);
		guiGraphics.drawString(font, Component.translatable("string.endermail.id"), 75, titleLabelY, 4210752, false);
	}

	@Override
	public void onClose() {
		if (!idTextField.getValue().isEmpty() && !idTextField.getValue().equals(lockerID)) {
			EnderMail.network.sendToServer(new ConfigureLockerPacket(lockerPos, idTextField.getValue()));
		}
		super.onClose();
	}

	@Override
	public boolean keyPressed(int par1, int par2, int par3) {
		if (par1 == 256 && shouldCloseOnEsc()) {
			onClose();
			return true;
		} else if (idTextField.canConsumeInput()) {
			return idTextField.keyPressed(par1, par2, par3);
		}
		return super.keyPressed(par1, par2, par3);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		int i = (width - imageWidth) / 2;
		int j = (height - imageHeight) / 2;
		guiGraphics.blit(TEXTURE, i, j, 0, 0, imageWidth, imageHeight);
	}

	@Override
	public void resize(Minecraft mc, int par2, int par3) {
		String s = idTextField.getValue();
		init(mc, par2, par3);
		idTextField.setValue(s);
	}

	private void setupTextFields() {
		clearWidgets();
		idTextField = new EditBox(font, (width - imageWidth) / 2 + 75, (height - imageHeight) / 2 + 20, 80, 18, Component.literal(""));
		idTextField.setMaxLength(LockerBlock.MAX_ID_LENGTH);
		idTextField.setValue(lockerID);
		addWidget(idTextField);
	}

}
