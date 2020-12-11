package com.chaosthedude.endermail.gui;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.blocks.LockerBlock;
import com.chaosthedude.endermail.gui.container.LockerContainer;
import com.chaosthedude.endermail.network.ConfigureLockerPacket;
import com.chaosthedude.endermail.util.RenderUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LockerScreen extends ContainerScreen<LockerContainer> implements IHasContainer<LockerContainer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("endermail:textures/gui/locker.png");

	private final LockerContainer containerLocker;
	private final PlayerInventory playerInventory;

	private TextFieldWidget idTextField;

	private BlockPos lockerPos;
	private String lockerID;

	public LockerScreen(LockerContainer containerLocker, PlayerInventory playerInventory, ITextComponent title) {
		super(containerLocker, playerInventory, title);
		this.playerInventory = playerInventory;
		this.containerLocker = containerLocker;
		this.lockerPos = containerLocker.getLockerPos();
		this.lockerID = containerLocker.getLockerID();
		ySize = 133;
	}

	@Override
	protected void init() {
		super.init();
		setupTextFields();
	}

	@Override
	public void tick() {
		super.tick();
		idTextField.tick();
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		idTextField.render(matrixStack, mouseX, mouseY, partialTicks);
		renderHoveredTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
		font.func_243248_b(matrixStack, new TranslationTextComponent("block.endermail.locker"), 8, 6, 4210752);
		font.func_243248_b(matrixStack, new TranslationTextComponent("string.endermail.id"), 75, 6, 4210752);
		font.func_243248_b(matrixStack, playerInventory.getDisplayName(), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	public void onClose() {
		if (!idTextField.getText().isEmpty() && !idTextField.getText().equals(lockerID)) {
			EnderMail.network.sendToServer(new ConfigureLockerPacket(lockerPos, idTextField.getText()));
		}
		super.onClose();
	}

	@Override
	public boolean keyPressed(int par1, int par2, int par3) {
		if (par1 == 256) {
			onClose();
			closeScreen();
			return true;
		} else if (par1 == 258) {
			boolean flag = !hasShiftDown();
			if (!changeFocus(flag)) {
				changeFocus(flag);
			}
			return true;
		} else if (getListener() != null && getListener().keyPressed(par1, par2, par3)) {
			return true;
		}
		InputMappings.Input mouseKey = InputMappings.getInputByCode(par1, par2);
		if (!idTextField.isFocused() && minecraft.gameSettings.keyBindInventory.isActiveAndMatches(mouseKey)) {
			onClose();
			closeScreen();
            return true;
         }
		if (itemStackMoved(par1, par2))
			return true;
		if (hoveredSlot != null && hoveredSlot.getHasStack()) {
			if (minecraft.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseKey)) {
				handleMouseClick(hoveredSlot, hoveredSlot.slotNumber, 0, ClickType.CLONE);
				return true;
			} else if (minecraft.gameSettings.keyBindDrop.isActiveAndMatches(mouseKey)) {
				handleMouseClick(hoveredSlot, this.hoveredSlot.slotNumber, hasControlDown() ? 1 : 0, ClickType.THROW);
				return true;
			}
		} else if (minecraft.gameSettings.keyBindDrop.isActiveAndMatches(mouseKey)) {
			return true;
		}
		return false;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		RenderUtils.drawTexturedModalRect(i, j, 0, 0, xSize, ySize);
	}

	private void setupTextFields() {
		children.clear();
		idTextField = new TextFieldWidget(font, (width - xSize) / 2 + 75, (height - ySize) / 2 + 20, 80, 18, new StringTextComponent(""));
		idTextField.setText(containerLocker.getLockerID() != null ? containerLocker.getLockerID() : "");
		idTextField.setMaxStringLength(LockerBlock.MAX_ID_LENGTH);
		children.add(idTextField);
	}

}
