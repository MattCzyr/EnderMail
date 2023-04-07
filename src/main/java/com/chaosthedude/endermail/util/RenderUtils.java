package com.chaosthedude.endermail.util;

import com.chaosthedude.endermail.config.ConfigHandler;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderUtils {

	private static final Minecraft mc = Minecraft.getInstance();
	private static final Font fontRenderer = mc.font;

	public static void drawStringLeft(PoseStack poseStack, String string, Font fontRenderer, int x, int y, int color) {
		fontRenderer.drawShadow(poseStack, string, x, y, color);
	}

	public static void drawStringRight(PoseStack poseStack, String string, Font fontRenderer, int x, int y, int color) {
		fontRenderer.drawShadow(poseStack, string, x - fontRenderer.width(string), y, color);
	}

	public static void drawConfiguredStringOnHUD(PoseStack poseStack, String string, int xOffset, int yOffset, int color, int relativeLineOffset) {
		final int lineOffset = ConfigHandler.CLIENT.lineOffset.get() + relativeLineOffset;
		yOffset += lineOffset * 9;
		if (ConfigHandler.CLIENT.overlaySide.get() == OverlaySide.LEFT) {
			drawStringLeft(poseStack, string, fontRenderer, xOffset + 2, yOffset + 2, color);
		} else {
			drawStringRight(poseStack, string, fontRenderer, mc.getWindow().getGuiScaledWidth() - xOffset - 2, yOffset + 2, color);
		}
	}

	public static void drawCenteredStringWithoutShadow(PoseStack poseStack, String string, int x, int y, int color) {
		fontRenderer.draw(poseStack, string, (float) (x - fontRenderer.width(string) / 2), (float) y, color);
	}

}
