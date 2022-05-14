package com.chaosthedude.endermail.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenWrapper {

	public static void openStampScreen(Level level, Player player, BlockPos packagePos) {
		Minecraft.getInstance().setScreen(new StampScreen(level, player, packagePos));
	}

}
