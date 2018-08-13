package com.chaosthedude.endermail.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.network.PacketStampPackage;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GuiStamp extends GuiScreen {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation("endermail:textures/gui/stamp.png");

	private GuiButton okButton;
	private GuiButton cancelButton;

	private GuiTextField xTextField;
	private GuiTextField yTextField;
	private GuiTextField zTextField;

	private World world;
	private EntityPlayer player;
	private BlockPos packagePos;

	private boolean errored;

	public GuiStamp(World world, EntityPlayer player, BlockPos packagePos) {
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
			if (button == okButton) {
				try {
					int x = Integer.valueOf(xTextField.getText());
					int y = -1;
					if (!yTextField.getText().isEmpty()) {
						y = Integer.valueOf(yTextField.getText());
					}
					int z = Integer.valueOf(zTextField.getText());
					BlockPos deliveryPos = new BlockPos(x, y, z);
					
					//BlockPackage blockPackage = (BlockPackage) world.getBlockState(packagePos).getBlock();
					//blockPackage.setState(true, world, packagePos);
					
					EnderMail.network.sendToServer(new PacketStampPackage(packagePos));
					mc.displayGuiScreen(null);
				} catch (NumberFormatException e) {
					errored = true;
				}
			} else if (button == cancelButton) {
				mc.displayGuiScreen(null);
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		int xSize = 178;
		int ySize = 222;
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		drawTexturedModalRect(i, j, 0, 0, xSize, ySize);
		drawCenteredString(fontRenderer, I18n.format("item.endermail.stamp.name"), width / 2, 20, 0xffffff);
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
		
		if (isNumeric(xTextField.getText()) && (yTextField.getText().isEmpty() || isNumeric(yTextField.getText())) && isNumeric(zTextField.getText())) {
			okButton.enabled = true;
		} else {
			okButton.enabled = false;
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
		cancelButton = addButton(new GuiButton(0, 20, height - 40, 20, 20, I18n.format("X")));
		okButton = addButton(new GuiButton(1, width - 40, height - 40, 20, 20, I18n.format("O")));
		okButton.enabled = false;
	}

	private void setupTextFields() {
		xTextField = new GuiTextField(0, fontRenderer, (width / 2) - 65, 80, 40, 20);
		yTextField = new GuiTextField(1, fontRenderer, (width / 2) - 20, 80, 40, 20);
		zTextField = new GuiTextField(2, fontRenderer, (width / 2) + 25, 80, 40, 20);
		
		xTextField.setFocused(true);
	}

	public static boolean isNumeric(String s) {
        if (s == null || s.length() == 0) {
            return false;
        }
        int size = s.length();
        for (int i = 0; i < size; i++) {
            if (!Character.isDigit(s.charAt(i)) && !(i == 0 && size > 1 && s.charAt(i) == '-')) {
                return false;
            }
        }
        return true;
    }

}
