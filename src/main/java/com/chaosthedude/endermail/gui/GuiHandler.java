package com.chaosthedude.endermail.gui;

import com.chaosthedude.endermail.blocks.te.TileEntityPackage;
import com.chaosthedude.endermail.gui.container.ContainerPackage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	
	public static final int PACKAGE_ID = 0;
	public static final int CONTROLLER_ID = 1;

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id == PACKAGE_ID) {
			TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
			if (te != null && te instanceof TileEntityPackage) {
				return new ContainerPackage(player.inventory, (TileEntityPackage) te, player);
			}
		} else if (id == CONTROLLER_ID) {
			// null
		}
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
		if (id == PACKAGE_ID) {
			if (te != null && te instanceof TileEntityPackage) {
				return new GuiPackage(player.inventory, (TileEntityPackage) te);
			}
		} else if (id == CONTROLLER_ID) {
			if (te != null && te instanceof TileEntityPackage) {
				return new GuiPackageController(world, player, new BlockPos(x, y, z));
			}
		}
		
		return null;
	}

}
