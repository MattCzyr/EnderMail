package com.chaosthedude.endermail.gui;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.blocks.LockerBlock;
import com.chaosthedude.endermail.gui.container.LockerContainer;
import com.chaosthedude.endermail.network.ConfigureLockerPacket;
import com.chaosthedude.endermail.util.RenderUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LockerScreen extends ContainerScreen<LockerContainer> implements IHasContainer<LockerContainer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("endermail:textures/gui/locker.png");

	private final LockerContainer containerLocker;
	private final PlayerInventory playerInventory;

	private TextFieldWidget idTextField;

	private BlockPos lockerPos;
	private String lockerID;

	public LockerScreen(LockerContainer containerLocker, PlayerInventory playerInventory, ITextComponent title) {
		super(containerLocker, playerInventory, title);
		this.playerInventory = playerInventory;
		this.containerLocker = containerLocker;
		this.lockerPos = containerLocker.getLockerPos();
		this.lockerID = containerLocker.getLockerID();
		ySize = 133;
	}

	@Override
	protected void func_231160_c_() {
		super.func_231160_c_();
		setupTextFields();
	}

	@Override
	public void func_231023_e_() {
		super.func_231023_e_();
		idTextField.tick();
	}

	@Override
	public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		func_230446_a_(matrixStack);
		super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
		idTextField.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
		func_230459_a_(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void func_230451_b_(MatrixStack matrixStack, int mouseX, int mouseY) {
		field_230712_o_.func_243248_b(matrixStack, new TranslationTextComponent("block.endermail.locker"), 8, 6, 4210752);
		field_230712_o_.func_243248_b(matrixStack, new TranslationTextComponent("string.endermail.id"), 75, 6, 4210752);
		field_230712_o_.func_243248_b(matrixStack, playerInventory.getDisplayName(), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	public void func_231175_as__() {
		if (!idTextField.getText().isEmpty() && !idTextField.getText().equals(lockerID)) {
			EnderMail.network.sendToServer(new ConfigureLockerPacket(lockerPos, idTextField.getText()));
		}
		super.func_231175_as__();
	}

	@Override
	public boolean func_231046_a_(int par1, int par2, int par3) {
		if (par1 == 256) {
			func_231175_as__();
			return true;
		} else if (par1 == 258) {
			boolean flag = !func_231173_s_();
			if (!func_231049_c__(flag)) {
				func_231049_c__(flag);
			}
			return true;
		} else if (func_241217_q_() != null && func_241217_q_().func_231046_a_(par1, par2, par3)) {
			return true;
		}
		InputMappings.Input mouseKey = InputMappings.getInputByCode(par1, par2);
		if (!idTextField.func_230999_j_() && field_230706_i_.gameSettings.keyBindInventory.isActiveAndMatches(mouseKey)) {
			func_231175_as__();
            return true;
         }
		if (func_195363_d(par1, par2))
			return true;
		if (hoveredSlot != null && hoveredSlot.getHasStack()) {
			if (field_230706_i_.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseKey)) {
				handleMouseClick(hoveredSlot, hoveredSlot.slotNumber, 0, ClickType.CLONE);
				return true;
			} else if (field_230706_i_.gameSettings.keyBindDrop.isActiveAndMatches(mouseKey)) {
				handleMouseClick(hoveredSlot, this.hoveredSlot.slotNumber, func_231172_r_() ? 1 : 0, ClickType.THROW);
				return true;
			}
		} else if (field_230706_i_.gameSettings.keyBindDrop.isActiveAndMatches(mouseKey)) {
			return true;
		}
		return false;
	}

	@Override
	protected void func_230450_a_(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		field_230706_i_.getTextureManager().bindTexture(TEXTURE);
		int i = (field_230708_k_ - xSize) / 2;
		int j = (field_230709_l_ - ySize) / 2;
		RenderUtils.drawTexturedModalRect(i, j, 0, 0, xSize, ySize);
	}

	private void setupTextFields() {
		field_230705_e_.clear();
		idTextField = new TextFieldWidget(field_230712_o_, (field_230708_k_ - xSize) / 2 + 75, (field_230709_l_ - ySize) / 2 + 20, 80, 18, new StringTextComponent(""));
		idTextField.setText(containerLocker.getLockerID() != null ? containerLocker.getLockerID() : "");
		idTextField.setMaxStringLength(LockerBlock.MAX_ID_LENGTH);
		field_230705_e_.add(idTextField);
	}

}
