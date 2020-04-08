package com.chaosthedude.endermail.gui;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.network.StampPackagePacket;
import com.chaosthedude.endermail.util.RenderUtils;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
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

	private TextFieldWidget xTextField;
	private TextFieldWidget yTextField;
	private TextFieldWidget zTextField;

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
		setupButtons();
		setupTextFields();
		minecraft.keyboardListener.enableRepeatEvents(true);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		int xSize = 178;
		int ySize = 222;
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		RenderUtils.drawTexturedModalRect(i, j, 0, 0, xSize, ySize);
		if (errored) {
			drawCenteredString(font, I18n.format("string.endermail.error"), width / 2, height - 65, 0xAAAAAA);
		}
		RenderUtils.drawCenteredStringWithoutShadow(I18n.format("string.endermail.deliveryLocation"), width / 2, 78, 0xAAAAAA);
		RenderUtils.drawCenteredStringWithoutShadow("X", (width / 2) - 45, 115, 0xAAAAAA);
		RenderUtils.drawCenteredStringWithoutShadow("Y", (width / 2) + 0, 115, 0xAAAAAA);
		RenderUtils.drawCenteredStringWithoutShadow("Z", (width / 2) + 45, 115, 0xAAAAAA);
		
		xTextField.render(mouseX, mouseY, partialTicks);
		yTextField.render(mouseX, mouseY, partialTicks);
		zTextField.render(mouseX, mouseY, partialTicks);

		super.render(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void tick() {
		super.tick();
		xTextField.tick();
		yTextField.tick();
		zTextField.tick();
		if (isNumeric(xTextField.getText()) && (yTextField.getText().isEmpty() || isNumeric(yTextField.getText())) && isNumeric(zTextField.getText())) {
			confirmButton.active = true;
		} else {
			confirmButton.active = false;
		}
	}
	
	@Override
	public void onClose() {
		super.onClose();
		minecraft.keyboardListener.enableRepeatEvents(false);
	}

	private void setupButtons() {
		buttons.clear();
		cancelButton = addButton(new Button(20, height - 40, 80, 20, I18n.format("string.endermail.cancel"), (onPress) -> {
			minecraft.displayGuiScreen(null);
		}));
		confirmButton = addButton(new Button(width - 100, height - 40, 80, 20, I18n.format("string.endermail.confirm"), (onPress) -> {
			try {
				int x = Integer.valueOf(xTextField.getText());
				int y = -1;
				if (!yTextField.getText().isEmpty()) {
					y = Integer.valueOf(yTextField.getText());
				}
				int z = Integer.valueOf(zTextField.getText());
				BlockPos deliveryPos = new BlockPos(x, y, z);

				EnderMail.network.sendToServer(new StampPackagePacket(packagePos, deliveryPos));
				minecraft.displayGuiScreen(null);
			} catch (NumberFormatException e) {
				errored = true;
			}
		}));
		confirmButton.active = false;
	}

	private void setupTextFields() {
		xTextField = new TextFieldWidget(font, (width / 2) - 65, 90, 40, 20, "");
		yTextField = new TextFieldWidget(font, (width / 2) - 20, 90, 40, 20, "");
		zTextField = new TextFieldWidget(font, (width / 2) + 25, 90, 40, 20, "");
	
		setFocused(xTextField);
		xTextField.setFocused2(true);
		
		children.add(xTextField);
		children.add(yTextField);
		children.add(zTextField);
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
