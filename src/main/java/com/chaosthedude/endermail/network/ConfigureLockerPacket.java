package com.chaosthedude.endermail.network;

import java.util.function.Supplier;

import com.chaosthedude.endermail.block.LockerBlock;
import com.chaosthedude.endermail.block.entity.LockerBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

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

	public ConfigureLockerPacket(FriendlyByteBuf buf) {
		lockerX = buf.readInt();
		lockerY = buf.readInt();
		lockerZ = buf.readInt();

		lockerID = buf.readUtf(LockerBlock.MAX_ID_LENGTH);
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeInt(lockerX);
		buf.writeInt(lockerY);
		buf.writeInt(lockerZ);

		buf.writeUtf(lockerID);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			BlockEntity blockEntity = ctx.get().getSender().getLevel().getBlockEntity(new BlockPos(lockerX, lockerY, lockerZ));
			if (blockEntity instanceof LockerBlockEntity) {
				LockerBlockEntity lockerBlockEntity = (LockerBlockEntity) blockEntity;
				lockerBlockEntity.setLockerID(lockerID);
				
			}
		});
		ctx.get().setPacketHandled(true);
	}

}
