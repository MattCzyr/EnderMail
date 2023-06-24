package com.chaosthedude.endermail.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class StampTextField extends EditBox {

	private Font font;
	private Component label;
	private int labelColor = 0x808080;

	private boolean pseudoIsEnabled = true;
	private boolean pseudoEnableBackgroundDrawing = true;
	private int pseudoMaxStringLength = 32;
	private int pseudoLineScrollOffset;
	private int pseudoEnabledColor = 0x6e6e6e;
	private int pseudoDisabledColor = 0xcfcfcf;
	private int pseudoCursorCounter;
	private int pseudoSelectionEnd;

	public StampTextField(Font font, int x, int y, int width, int height, Component label) {
		super(font, x, y, width, height, label);
		this.font = font;
		this.label = label;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		if (isVisible()) {
			if (pseudoEnableBackgroundDrawing) {
				final int color = (int) (255.0F * 0.25f);
				guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), color / 2 << 24);
			}
			boolean showLabel = !isFocused() && getValue().isEmpty();
			int i = showLabel ? labelColor : (pseudoIsEnabled ? pseudoEnabledColor : pseudoDisabledColor);
			int j = getCursorPosition() - pseudoLineScrollOffset;
			int k = pseudoSelectionEnd - pseudoLineScrollOffset;
			String text = showLabel ? label.getString() : getValue();
			String s = font.plainSubstrByWidth(text.substring(pseudoLineScrollOffset), width, false);
			boolean flag = j >= 0 && j <= s.length();
			boolean flag1 = isFocused() && pseudoCursorCounter / 6 % 2 == 0 && flag;
			int l = pseudoEnableBackgroundDrawing ? getX() + 4 : getX();
			int i1 = pseudoEnableBackgroundDrawing ? getY() + (getHeight() - 8) / 2 : getY();
			int j1 = l;

			if (k > s.length()) {
				k = s.length();
			}

			if (!s.isEmpty()) {
				String s1 = flag ? s.substring(0, j) : s;
				j1 = guiGraphics.drawString(font, s1, l, i1, i, false);
			}

			boolean flag2 = getCursorPosition() < getValue().length() || getValue().length() >= pseudoMaxStringLength;
			int k1 = j1;

			if (!flag) {
				k1 = j > 0 ? l + width : l;
			} else if (flag2) {
				k1 = j1 - 1;
				--j1;
			}

			if (!s.isEmpty() && flag && j < s.length()) {
				j1 = guiGraphics.drawString(font, s.substring(j), j1, i1, i, false);
			}

			if (flag1) {
				if (flag2) {
					guiGraphics.fill(RenderType.guiOverlay(), k1, i1 - 1, k1 + 1, i1 + 1 + font.lineHeight, -3092272);
				} else {
					guiGraphics.drawString(font, "_", k1, i1, i, false);
				}
			}

			if (k != j) {
				int l1 = l + font.width(s.substring(0, k));
				drawSelectionBox(guiGraphics, k1, i1 - 1, l1 - 1, i1 + 1 + font.lineHeight);
			}
		}
	}
	
	@Override
	public void setEditable(boolean enabled) {
		super.setEditable(enabled);
		pseudoIsEnabled = enabled;
	}

	@Override
	public void setTextColor(int color) {
		super.setTextColor(color);
		pseudoEnabledColor = color;
	}

	@Override
	public void setTextColorUneditable(int color) {
		super.setTextColorUneditable(color);
		pseudoDisabledColor = color;
	}

	@Override
	public void setFocused(boolean isFocused) {
		if (isFocused && !isFocused()) {
			pseudoCursorCounter = 0;
		}
		super.setFocused(isFocused);
	}
	
	@Override
	public void setBordered(boolean enableBackgroundDrawing) {
		super.setBordered(enableBackgroundDrawing);
		pseudoEnableBackgroundDrawing = enableBackgroundDrawing;
	}
	
	@Override
	public void setMaxLength(int length) {
		super.setMaxLength(length);
		pseudoMaxStringLength = length;
	}
	
	@Override
	public void tick() {
		super.tick();
		pseudoCursorCounter++;
	}
	
	@Override
	public void setHighlightPos(int position) {
		super.setHighlightPos(position);
		int i = getValue().length();
		pseudoSelectionEnd = Mth.clamp(position, 0, i);
		if (font != null) {
			if (pseudoLineScrollOffset > i) {
				pseudoLineScrollOffset = i;
			}

			int j = getInnerWidth();
			String s = font.plainSubstrByWidth(getValue().substring(this.pseudoLineScrollOffset), j, false);
			int k = s.length() + pseudoLineScrollOffset;
			if (pseudoSelectionEnd == pseudoLineScrollOffset) {
				pseudoLineScrollOffset -= font.plainSubstrByWidth(getValue(), j, true).length();
			}

			if (pseudoSelectionEnd > k) {
				pseudoLineScrollOffset += pseudoSelectionEnd - k;
			} else if (pseudoSelectionEnd <= pseudoLineScrollOffset) {
				pseudoLineScrollOffset -= pseudoLineScrollOffset - pseudoSelectionEnd;
			}

			pseudoLineScrollOffset = Mth.clamp(pseudoLineScrollOffset, 0, i);
		}
	}

	public void setLabel(Component label) {
		this.label = label;
	}

	public void setLabelColor(int labelColor) {
		this.labelColor = labelColor;
	}

	private void drawSelectionBox(GuiGraphics guiGraphics, int startX, int startY, int endX, int endY) {
		if (startX < endX) {
			int i = startX;
			startX = endX;
			endX = i;
		}

		if (startY < endY) {
			int j = startY;
			startY = endY;
			endY = j;
		}

		if (endX > getX() + getWidth()) {
			endX = getX() + getWidth();
		}

		if (startX > getX() + getWidth()) {
			startX = getX() + getWidth();
		}

		guiGraphics.fill(RenderType.guiTextHighlight(), startX, startY, endX, endY, -16776961);
	}
	
}
