package com.chaosthedude.endermail.util;

import com.chaosthedude.endermail.config.ConfigHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderUtils {

	private static final Minecraft mc = Minecraft.getMinecraft();
	private static final FontRenderer fontRenderer = mc.fontRenderer;

	public static void drawStringLeft(String string, FontRenderer fontRenderer, int x, int y, int color) {
		fontRenderer.drawString(string, x, y, color, true);
	}
    
	public static void drawStringRight(String string, FontRenderer fontRenderer, int x, int y, int color) {
		fontRenderer.drawString(string, x - fontRenderer.getStringWidth(string), y, color, true);
	}

	public static void drawConfiguredStringOnHUD(String string, int xOffset, int yOffset, int color, int relativeLineOffset) {
		final ScaledResolution resolution = new ScaledResolution(mc);
		final int lineOffset = ConfigHandler.lineOffset + relativeLineOffset;

		yOffset += lineOffset * 9;
		if (ConfigHandler.overlaySide == EnumOverlaySide.LEFT) {
			drawStringLeft(string, fontRenderer, xOffset + 2, yOffset + 2, color);
		} else {
			drawStringRight(string, fontRenderer, resolution.getScaledWidth() - xOffset - 2, yOffset + 2, color);
		}
	}
	
	public static void drawCenteredStringWithoutShadow(String string, int x, int y, int color) {
        fontRenderer.drawString(string, (float)(x - fontRenderer.getStringWidth(string) / 2), (float) y, color, false);
    }

	public static void drawRect(int left, int top, int right, int bottom, int color) {
		if (left < right) {
			int temp = left;
			left = right;
			right = temp;
		}

		if (top < bottom) {
			int temp = top;
			top = bottom;
			bottom = temp;
		}

		final float red = (float) (color >> 16 & 255) / 255.0F;
		final float green = (float) (color >> 8 & 255) / 255.0F;
		final float blue = (float) (color & 255) / 255.0F;
		final float alpha = (float) (color >> 24 & 255) / 255.0F;

		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder buffer = tessellator.getBuffer();

		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(red, green, blue, alpha);

		buffer.begin(7, DefaultVertexFormats.POSITION);
		buffer.pos((double) left, (double) bottom, 0.0D).endVertex();
		buffer.pos((double) right, (double) bottom, 0.0D).endVertex();
		buffer.pos((double) right, (double) top, 0.0D).endVertex();
		buffer.pos((double) left, (double) top, 0.0D).endVertex();
		tessellator.draw();

		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

}
