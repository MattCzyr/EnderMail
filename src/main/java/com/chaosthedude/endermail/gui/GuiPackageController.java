package com.chaosthedude.endermail.gui;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.network.PacketSpawnMailman;
import com.chaosthedude.endermail.registry.EnderMailBlocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GuiPackageController extends GuiScreen {

	private GuiButton deliverButton;
	private GuiButton closeButton;

	private GuiTextField xTextField;
	private GuiTextField yTextField;
	private GuiTextField zTextField;

	private World world;
	private EntityPlayer player;
	private BlockPos packagePos;

	private boolean errored;

	public GuiPackageController(World world, EntityPlayer player, BlockPos packagePos) {
		this.world = world;
		this.player = player;
		this.packagePos = packagePos;
	}

	@Override
	public void initGui() {
		setupButtons();
		setupTextFields();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.enabled) {
			if (button == deliverButton) {
				try {
					int x = Integer.valueOf(xTextField.getText());
					int y = -1;
					if (!yTextField.getText().isEmpty()) {
						y = Integer.valueOf(yTextField.getText());
					}
					int z = Integer.valueOf(zTextField.getText());
					BlockPos deliveryPos = new BlockPos(x, y, z);
					
					IBlockState iblockstate = world.getBlockState(packagePos);
					TileEntity tileentity = world.getTileEntity(packagePos);
					tileentity.setPos(deliveryPos);
					world.setBlockState(packagePos, EnderMailBlocks.blockStampedPackage.getDefaultState(), 3);
					if (tileentity != null) {
						tileentity.validate();
						world.setTileEntity(packagePos, tileentity);
					}
					
					EnderMail.network.sendToServer(new PacketSpawnMailman(packagePos, deliveryPos));
					mc.displayGuiScreen(null);
				} catch (NumberFormatException e) {
					errored = true;
				}
			} else if (button == closeButton) {
				mc.displayGuiScreen(null);
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		drawCenteredString(fontRenderer, I18n.format("item.endermail.package_controller.name"), width / 2, 20, 0xffffff);
		if (errored) {
			drawCenteredString(fontRenderer, I18n.format("string.endermail.error"), width / 2, height - 65, 0xffffff);
		}
		
		xTextField.drawTextBox();
		yTextField.drawTextBox();
		zTextField.drawTextBox();

		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		xTextField.updateCursorCounter();
		yTextField.updateCursorCounter();
		zTextField.updateCursorCounter();
		
		if (StringUtils.isNumeric(xTextField.getText()) && (yTextField.getText().isEmpty() || StringUtils.isNumeric(yTextField.getText())) && StringUtils.isNumeric(zTextField.getText())) {
			deliverButton.enabled = true;
		} else {
			deliverButton.enabled = false;
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);

		if (Character.isDigit(typedChar) || keyCode == Keyboard.KEY_BACK) {
			xTextField.textboxKeyTyped(typedChar, keyCode);
			yTextField.textboxKeyTyped(typedChar, keyCode);
			zTextField.textboxKeyTyped(typedChar, keyCode);
			errored = false;
		} else if (typedChar == '-') {
			if (xTextField.getCursorPosition() == 0) {
				xTextField.textboxKeyTyped(typedChar, keyCode);
				errored = false;
			} else if (yTextField.getCursorPosition() == 0) {
				yTextField.textboxKeyTyped(typedChar, keyCode);
				errored = false;
			} else if (zTextField.getCursorPosition() == 0) {
				zTextField.textboxKeyTyped(typedChar, keyCode);
				errored = false;
			}
		} else if (keyCode == Keyboard.KEY_TAB) {
			if (xTextField.isFocused()) {
				xTextField.setFocused(false);
				yTextField.setFocused(true);
			} else if (yTextField.isFocused()) {
				yTextField.setFocused(false);
				zTextField.setFocused(true);
			} else if (zTextField.isFocused()) {
				zTextField.setFocused(false);
				xTextField.setFocused(true);
			}
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		xTextField.mouseClicked(mouseX, mouseY, mouseButton);
		yTextField.mouseClicked(mouseX, mouseY, mouseButton);
		zTextField.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public boolean doesGuiPauseGame() {
        return false;
    }

	@Override
	protected <T extends GuiButton> T addButton(T button) {
		buttonList.add(button);
		return (T) button;
	}

	private void setupButtons() {
		buttonList.clear();
		closeButton = addButton(new GuiButton(0, width / 2 - 154, height - 52, 150, 20, I18n.format("string.endermail.close")));
		deliverButton = addButton(new GuiButton(1, width / 2 + 4, height - 52, 150, 20, I18n.format("string.endermail.deliver")));
		deliverButton.enabled = false;
	}

	private void setupTextFields() {
		xTextField = new GuiTextField(0, fontRenderer, (width / 2) - 65, 80, 40, 20);
		yTextField = new GuiTextField(1, fontRenderer, (width / 2) - 20, 80, 40, 20);
		zTextField = new GuiTextField(2, fontRenderer, (width / 2) + 25, 80, 40, 20);
		
		xTextField.setFocused(true);
	}

}
