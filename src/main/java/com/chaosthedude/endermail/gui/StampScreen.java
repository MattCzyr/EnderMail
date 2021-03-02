package com.chaosthedude.endermail.gui;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.blocks.LockerBlock;
import com.chaosthedude.endermail.network.StampPackagePacket;
import com.chaosthedude.endermail.util.RenderUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StampScreen extends Screen {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation("endermail:textures/gui/stamp.png");

	private Button confirmButton;
	private Button cancelButton;

	private StampTextField xTextField;
	private StampTextField yTextField;
	private StampTextField zTextField;
	
	private StampTextField lockerIDTextField;

	private World world;
	private PlayerEntity player;
	private BlockPos packagePos;

	private boolean errored;

	public StampScreen(World world, PlayerEntity player, BlockPos packagePos) {
		super(new StringTextComponent(""));
		this.world = world;
		this.player = player;
		this.packagePos = packagePos;
	}

	@Override
	public void init() {
		setupTextFields();
		setupButtons();
		minecraft.keyboardListener.enableRepeatEvents(true);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (ConfigHandler.GENERAL.disableDeliveryLocation.get()) {
			int xSize = 178;
			int ySize = 180;
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			minecraft.getTextureManager().bindTexture(TEXTURE);
			int i = (width - xSize) / 2;
			int j = (height - ySize) / 2;
			RenderUtils.drawTexturedModalRect(i, j, 0, 0, xSize, ySize);
			if (errored) {
				RenderUtils.drawCenteredStringWithoutShadow(matrixStack, I18n.format("string.endermail.error"), width / 2, height - 65, 0xAAAAAA);
			}
			
			RenderUtils.drawCenteredStringWithoutShadow(matrixStack, I18n.format("string.endermail.lockerID"), width / 2, height / 2 + 13, 0xAAAAAA);
			
			lockerIDTextField.render(matrixStack, mouseX, mouseY, partialTicks);
		} else {
			int xSize = 178;
			int ySize = 222;
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			minecraft.getTextureManager().bindTexture(TEXTURE);
			int i = (width - xSize) / 2;
			int j = (height - ySize) / 2;
			RenderUtils.drawTexturedModalRect(i, j, 0, 0, xSize, ySize);
			if (errored) {
				RenderUtils.drawCenteredStringWithoutShadow(matrixStack, I18n.format("string.endermail.error"), width / 2, height - 65, 0xAAAAAA);
			}
			
			RenderUtils.drawCenteredStringWithoutShadow(matrixStack, I18n.format("string.endermail.deliveryLocation"), width / 2, height / 2 - 42, 0xAAAAAA);
			RenderUtils.drawCenteredStringWithoutShadow(matrixStack, "X", (width / 2) - 45, height / 2 - 5, 0xAAAAAA);
			RenderUtils.drawCenteredStringWithoutShadow(matrixStack, "Y", (width / 2) + 0, height / 2 - 5, 0xAAAAAA);
			RenderUtils.drawCenteredStringWithoutShadow(matrixStack, "Z", (width / 2) + 45, height / 2 - 5, 0xAAAAAA);
			
			RenderUtils.drawCenteredStringWithoutShadow(matrixStack, I18n.format("string.endermail.lockerID"), width / 2, height / 2 + 13, 0xAAAAAA);
			
			lockerIDTextField.render(matrixStack, mouseX, mouseY, partialTicks);
			
			xTextField.render(matrixStack, mouseX, mouseY, partialTicks);
			yTextField.render(matrixStack, mouseX, mouseY, partialTicks);
			zTextField.render(matrixStack, mouseX, mouseY, partialTicks);
		}

		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void tick() {
		super.tick();
		lockerIDTextField.tick();
		xTextField.tick();
		yTextField.tick();
		zTextField.tick();
		confirmButton.active = (!lockerIDTextField.getText().isEmpty() && xTextField.getText().isEmpty() && yTextField.getText().isEmpty() && zTextField.getText().isEmpty()) || (isNumeric(xTextField.getText()) && (yTextField.getText().isEmpty() || isNumeric(yTextField.getText())) && isNumeric(zTextField.getText()));
	}
	
	@Override
	public void onClose() {
		super.onClose();
		minecraft.keyboardListener.enableRepeatEvents(false);
	}

	private void setupButtons() {
		buttons.clear();
		cancelButton = addButton(new Button(20, height - 40, 80, 20, new StringTextComponent(I18n.format("string.endermail.cancel")), (onPress) -> {
			minecraft.displayGuiScreen(null);
		}));
		confirmButton = addButton(new Button(width - 100, height - 40, 80, 20, new StringTextComponent(I18n.format("string.endermail.confirm")), (onPress) -> {
			try {
				String lockerID = lockerIDTextField.getText();
				int x = -1;
				int y = -1;
				int z = -1;
				if (lockerID.isEmpty() || (!xTextField.getText().isEmpty() && !yTextField.getText().isEmpty() && !zTextField.getText().isEmpty())) {
					x = Integer.valueOf(xTextField.getText());
					if (!yTextField.getText().isEmpty()) {
						y = Integer.valueOf(yTextField.getText());
					}
					z = Integer.valueOf(zTextField.getText());
				}
				BlockPos deliveryPos = new BlockPos(x, y, z);

				EnderMail.network.sendToServer(new StampPackagePacket(packagePos, deliveryPos, lockerID, !xTextField.getText().isEmpty() && !zTextField.getText().isEmpty()));
				minecraft.displayGuiScreen(null);
			} catch (NumberFormatException e) {
				errored = true;
			}
		}));
		confirmButton.active = false;
	}

	private void setupTextFields() {
		children.clear();
		
		xTextField = new StampTextField(font, (width / 2) - 65, height / 2 - 30, 40, 20, new StringTextComponent(""));
		yTextField = new StampTextField(font, (width / 2) - 20, height / 2 - 30, 40, 20, new StringTextComponent(""));
		zTextField = new StampTextField(font, (width / 2) + 25, height / 2 - 30, 40, 20, new StringTextComponent(""));
		
		lockerIDTextField = new StampTextField(font, (width / 2) - 65, (height / 2) + 25, 130, 20, new StringTextComponent(""));
		lockerIDTextField.setMaxStringLength(LockerBlock.MAX_ID_LENGTH);
	
		setFocusedDefault(xTextField);
		xTextField.setFocused2(true);
		
		children.add(xTextField);
		children.add(yTextField);
		children.add(zTextField);
		children.add(lockerIDTextField);
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
