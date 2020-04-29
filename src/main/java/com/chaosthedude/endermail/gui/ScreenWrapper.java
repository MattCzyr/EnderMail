package com.chaosthedude.endermail.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenWrapper {

	public static void openStampScreen(World world, PlayerEntity player, BlockPos packagePos) {
		Minecraft.getInstance().displayGuiScreen(new StampScreen(world, player, packagePos));
	}

}
