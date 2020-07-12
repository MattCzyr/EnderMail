package com.chaosthedude.endermail.gui;

import com.chaosthedude.endermail.EnderMail;
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
	public void func_231160_c_() {
		setupTextFields();
		setupButtons();
		field_230706_i_.keyboardListener.enableRepeatEvents(true);
	}

	@Override
	public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		int xSize = 178;
		int ySize = 222;
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		field_230706_i_.getTextureManager().bindTexture(TEXTURE);
		int i = (field_230708_k_ - xSize) / 2;
		int j = (field_230709_l_ - ySize) / 2;
		RenderUtils.drawTexturedModalRect(i, j, 0, 0, xSize, ySize);
		if (errored) {
			RenderUtils.drawCenteredStringWithoutShadow(matrixStack, I18n.format("string.endermail.error"), field_230708_k_ / 2, field_230709_l_ - 65, 0xAAAAAA);
		}
		
		RenderUtils.drawCenteredStringWithoutShadow(matrixStack, I18n.format("string.endermail.deliveryLocation"), field_230708_k_ / 2, field_230709_l_ / 2 - 42, 0xAAAAAA);
		RenderUtils.drawCenteredStringWithoutShadow(matrixStack, "X", (field_230708_k_ / 2) - 45, field_230709_l_ / 2 - 5, 0xAAAAAA);
		RenderUtils.drawCenteredStringWithoutShadow(matrixStack, "Y", (field_230708_k_ / 2) + 0, field_230709_l_ / 2 - 5, 0xAAAAAA);
		RenderUtils.drawCenteredStringWithoutShadow(matrixStack, "Z", (field_230708_k_ / 2) + 45, field_230709_l_ / 2 - 5, 0xAAAAAA);
		
		RenderUtils.drawCenteredStringWithoutShadow(matrixStack, I18n.format("string.endermail.lockerID"), field_230708_k_ / 2, field_230709_l_ / 2 + 13, 0xAAAAAA);
		
		lockerIDTextField.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
		
		xTextField.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
		yTextField.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
		zTextField.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);

		super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void func_231023_e_() {
		super.func_231023_e_();
		lockerIDTextField.tick();
		xTextField.tick();
		yTextField.tick();
		zTextField.tick();
		confirmButton.field_230693_o_ = (!lockerIDTextField.getText().isEmpty() && xTextField.getText().isEmpty() && yTextField.getText().isEmpty() && zTextField.getText().isEmpty()) || (isNumeric(xTextField.getText()) && (yTextField.getText().isEmpty() || isNumeric(yTextField.getText())) && isNumeric(zTextField.getText()));
	}
	
	@Override
	public void func_231175_as__() {
		super.func_231175_as__();
		field_230706_i_.keyboardListener.enableRepeatEvents(false);
	}

	private void setupButtons() {
		field_230710_m_.clear();
		cancelButton = func_230480_a_(new Button(20, field_230709_l_ - 40, 80, 20, new StringTextComponent(I18n.format("string.endermail.cancel")), (onPress) -> {
			field_230706_i_.displayGuiScreen(null);
		}));
		confirmButton = func_230480_a_(new Button(field_230708_k_ - 100, field_230709_l_ - 40, 80, 20, new StringTextComponent(I18n.format("string.endermail.confirm")), (onPress) -> {
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

				EnderMail.network.sendToServer(new StampPackagePacket(packagePos, deliveryPos, lockerID));
				field_230706_i_.displayGuiScreen(null);
			} catch (NumberFormatException e) {
				errored = true;
			}
		}));
		confirmButton.field_230693_o_ = false;
	}

	private void setupTextFields() {
		field_230705_e_.clear();
		
		xTextField = new StampTextField(field_230712_o_, (field_230708_k_ / 2) - 65, field_230709_l_ / 2 - 30, 40, 20, new StringTextComponent(""));
		yTextField = new StampTextField(field_230712_o_, (field_230708_k_ / 2) - 20, field_230709_l_ / 2 - 30, 40, 20, new StringTextComponent(""));
		zTextField = new StampTextField(field_230712_o_, (field_230708_k_ / 2) + 25, field_230709_l_ / 2 - 30, 40, 20, new StringTextComponent(""));
		
		lockerIDTextField = new StampTextField(field_230712_o_, (field_230708_k_ / 2) - 65, (field_230709_l_ / 2) + 25, 130, 20, new StringTextComponent(""));
	
		setFocusedDefault(xTextField);
		xTextField.setFocused2(true);
		
		field_230705_e_.add(xTextField);
		field_230705_e_.add(yTextField);
		field_230705_e_.add(zTextField);
		field_230705_e_.add(lockerIDTextField);
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
