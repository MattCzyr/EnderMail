package com.chaosthedude.endermail.util;

import com.chaosthedude.endermail.config.ConfigHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderUtils {

	private static final Minecraft mc = Minecraft.getInstance();
	private static final FontRenderer fontRenderer = mc.fontRenderer;

	public static void drawStringLeft(String string, FontRenderer fontRenderer, int x, int y, int color) {
		fontRenderer.drawString(string, x, y, color);
	}

	public static void drawStringRight(String string, FontRenderer fontRenderer, int x, int y, int color) {
		fontRenderer.drawString(string, x - fontRenderer.getStringWidth(string), y, color);
	}

	public static void drawConfiguredStringOnHUD(String string, int xOffset, int yOffset, int color, int relativeLineOffset) {
		final int lineOffset = ConfigHandler.CLIENT.lineOffset.get() + relativeLineOffset;
		yOffset += lineOffset * 9;
		if (ConfigHandler.CLIENT.overlaySide.get() == OverlaySide.LEFT) {
			drawStringLeft(string, fontRenderer, xOffset + 2, yOffset + 2, color);
		} else {
			drawStringRight(string, fontRenderer, mc.getMainWindow().getScaledWidth() - xOffset - 2, yOffset + 2, color);
		}
	}

	public static void drawCenteredStringWithoutShadow(String string, int x, int y, int color) {
		fontRenderer.drawString(string, (float) (x - fontRenderer.getStringWidth(string) / 2), (float) y, color);
	}

	public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double) (x + 0), (double) (y + height), 0).tex(((float) (textureX + 0) * 0.00390625F), ((float) (textureY + height) * 0.00390625F)).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + height), 0).tex(((float) (textureX + width) * 0.00390625F), ((float) (textureY + height) * 0.00390625F)).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + 0), 0).tex(((float) (textureX + width) * 0.00390625F), ((float) (textureY + 0) * 0.00390625F)).endVertex();
		bufferbuilder.pos((double) (x + 0), (double) (y + 0), 0).tex(((float) (textureX + 0) * 0.00390625F), ((float) (textureY + 0) * 0.00390625F)).endVertex();
		tessellator.draw();
	}

}
