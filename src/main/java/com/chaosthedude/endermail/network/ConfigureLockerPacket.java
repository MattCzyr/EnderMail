package com.chaosthedude.endermail.network;

import java.util.function.Supplier;

import com.chaosthedude.endermail.blocks.LockerBlock;
import com.chaosthedude.endermail.blocks.te.LockerTileEntity;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class ConfigureLockerPacket {
	
	private int lockerX;
	private int lockerY;
	private int lockerZ;
	private String lockerID;

	public ConfigureLockerPacket() {
	}

	public ConfigureLockerPacket(BlockPos lockerPos, String lockerID) {
		this.lockerX = lockerPos.getX();
		this.lockerY = lockerPos.getY();
		this.lockerZ = lockerPos.getZ();
		this.lockerID = lockerID;
	}

	public ConfigureLockerPacket(PacketBuffer buf) {
		lockerX = buf.readInt();
		lockerY = buf.readInt();
		lockerZ = buf.readInt();

		lockerID = buf.readString(LockerBlock.MAX_ID_LENGTH);
	}

	public void toBytes(PacketBuffer buf) {
		buf.writeInt(lockerX);
		buf.writeInt(lockerY);
		buf.writeInt(lockerZ);

		buf.writeString(lockerID);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			TileEntity te = ctx.get().getSender().world.getTileEntity(new BlockPos(lockerX, lockerY, lockerZ));
			if (te instanceof LockerTileEntity) {
				LockerTileEntity lockerTe = (LockerTileEntity) te;
				lockerTe.setLockerID(lockerID);
				
			}
		});
		ctx.get().setPacketHandled(true);
	}

}
