package com.chaosthedude.endermail.util;

import com.chaosthedude.endermail.config.ConfigHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderUtils {

	private static final Minecraft mc = Minecraft.getInstance();
	private static final Font font = mc.font;

	public static void drawStringLeft(GuiGraphics guiGraphics, String string, Font fontRenderer, int x, int y, int color) {
		guiGraphics.drawString(font, string, x, y, color);
	}

	public static void drawStringRight(GuiGraphics guiGraphics, String string, Font fontRenderer, int x, int y, int color) {
		guiGraphics.drawString(font, string, x - fontRenderer.width(string), y, color);
	}

	public static void drawConfiguredStringOnHUD(GuiGraphics guiGraphics, String string, int xOffset, int yOffset, int color, int relativeLineOffset) {
		final int lineOffset = ConfigHandler.CLIENT.lineOffset.get() + relativeLineOffset;
		yOffset += lineOffset * 9;
		if (ConfigHandler.CLIENT.overlaySide.get() == OverlaySide.LEFT) {
			drawStringLeft(guiGraphics, string, font, xOffset + 2, yOffset + 2, color);
		} else {
			drawStringRight(guiGraphics, string, font, mc.getWindow().getGuiScaledWidth() - xOffset - 2, yOffset + 2, color);
		}
	}

	public static void drawCenteredStringWithoutShadow(GuiGraphics guiGraphics, String string, int x, int y, int color) {
		guiGraphics.drawString(font, string, x - font.width(string) / 2, y, color);
	}

}
