package com.chaosthedude.endermail.gui;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.block.LockerBlock;
import com.chaosthedude.endermail.network.StampPackagePacket;
import com.chaosthedude.endermail.util.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StampScreen extends Screen {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnderMail.MODID, "textures/gui/stamp.png");

	private Button confirmButton;
	private Button cancelButton;

	private StampTextField xTextField;
	private StampTextField yTextField;
	private StampTextField zTextField;
	
	private StampTextField lockerIDTextField;

	private Level level;
	private Player player;
	private BlockPos packagePos;

	private boolean errored;

	public StampScreen(Level level, Player player, BlockPos packagePos) {
		super(Component.literal(""));
		this.level = level;
		this.player = player;
		this.packagePos = packagePos;
	}

	@Override
	public void init() {
		setupWidgets();
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		int xSize = 178;
		int ySize = 222;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
	    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	    RenderSystem.setShaderTexture(0, TEXTURE);
		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		blit(poseStack, i, j, 0, 0, xSize, ySize);
		if (errored) {
			RenderUtils.drawCenteredStringWithoutShadow(poseStack, I18n.get("string.endermail.error"), width / 2, height - 65, 0xAAAAAA);
		}
		
		RenderUtils.drawCenteredStringWithoutShadow(poseStack, I18n.get("string.endermail.deliveryLocation"), width / 2, height / 2 - 42, 0xAAAAAA);
		RenderUtils.drawCenteredStringWithoutShadow(poseStack, "X", (width / 2) - 45, height / 2 - 5, 0xAAAAAA);
		RenderUtils.drawCenteredStringWithoutShadow(poseStack, "Y", (width / 2) + 0, height / 2 - 5, 0xAAAAAA);
		RenderUtils.drawCenteredStringWithoutShadow(poseStack, "Z", (width / 2) + 45, height / 2 - 5, 0xAAAAAA);
		
		RenderUtils.drawCenteredStringWithoutShadow(poseStack, I18n.get("string.endermail.lockerID"), width / 2, height / 2 + 13, 0xAAAAAA);
		
		lockerIDTextField.render(poseStack, mouseX, mouseY, partialTicks);
		
		xTextField.render(poseStack, mouseX, mouseY, partialTicks);
		yTextField.render(poseStack, mouseX, mouseY, partialTicks);
		zTextField.render(poseStack, mouseX, mouseY, partialTicks);

		super.render(poseStack, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void tick() {
		super.tick();
		lockerIDTextField.tick();
		xTextField.tick();
		yTextField.tick();
		zTextField.tick();
		confirmButton.active = (!lockerIDTextField.getValue().isEmpty() && xTextField.getValue().isEmpty() && yTextField.getValue().isEmpty() && zTextField.getValue().isEmpty()) || (isNumeric(xTextField.getValue()) && (yTextField.getValue().isEmpty() || isNumeric(yTextField.getValue())) && isNumeric(zTextField.getValue()));
	}

	private void setupWidgets() {
		clearWidgets();
		cancelButton = addRenderableWidget(Button.builder(Component.translatable("string.endermail.cancel"), (onPress) -> {
			minecraft.setScreen(null);
		}).bounds(20, height - 40, 80, 20).build());
		confirmButton = addRenderableWidget(Button.builder(Component.translatable("string.endermail.confirm"), (onPress) -> {
			try {
				String lockerID = lockerIDTextField.getValue();
				int x = -1;
				int y = -1;
				int z = -1;
				if (lockerID.isEmpty() || (!xTextField.getValue().isEmpty() && !yTextField.getValue().isEmpty() && !zTextField.getValue().isEmpty())) {
					x = Integer.valueOf(xTextField.getValue());
					if (!yTextField.getValue().isEmpty()) {
						y = Integer.valueOf(yTextField.getValue());
					}
					z = Integer.valueOf(zTextField.getValue());
				}
				BlockPos deliveryPos = new BlockPos(x, y, z);

				EnderMail.network.sendToServer(new StampPackagePacket(packagePos, deliveryPos, lockerID, !xTextField.getValue().isEmpty() && !zTextField.getValue().isEmpty()));
				minecraft.setScreen(null);
			} catch (NumberFormatException e) {
				errored = true;
			}
		}).bounds(width - 100, height - 40, 80, 20).build());
		confirmButton.active = false;
		
		xTextField = addRenderableWidget(new StampTextField(font, (width / 2) - 65, height / 2 - 30, 40, 20, Component.literal("")));
		yTextField = addRenderableWidget(new StampTextField(font, (width / 2) - 20, height / 2 - 30, 40, 20, Component.literal("")));
		zTextField = addRenderableWidget(new StampTextField(font, (width / 2) + 25, height / 2 - 30, 40, 20, Component.literal("")));
		
		lockerIDTextField = addRenderableWidget(new StampTextField(font, (width / 2) - 65, (height / 2) + 25, 130, 20, Component.literal("")));
		lockerIDTextField.setMaxLength(LockerBlock.MAX_ID_LENGTH);
	
		setInitialFocus(xTextField);
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
