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

		final Tesselator tesselator = Tesselator.getInstance();
		final BufferBuilder buffer = tesselator.getBuilder();

		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderSystem.setShaderColor(red, green, blue, alpha);

		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
		buffer.vertex((double) left, (double) bottom, 0.0D).endVertex();
		buffer.vertex((double) right, (double) bottom, 0.0D).endVertex();
		buffer.vertex((double) right, (double) top, 0.0D).endVertex();
		buffer.vertex((double) left, (double) top, 0.0D).endVertex();
		tesselator.end();

		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

}
