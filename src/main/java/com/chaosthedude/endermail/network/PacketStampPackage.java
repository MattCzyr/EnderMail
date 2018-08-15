package com.chaosthedude.endermail.network;

import java.util.List;

import com.chaosthedude.endermail.blocks.BlockPackage;
import com.chaosthedude.endermail.entity.EntityEnderMailman;
import com.chaosthedude.endermail.registry.EnderMailItems;
import com.chaosthedude.endermail.util.ItemUtils;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketStampPackage implements IMessage {

	private int packageX;
	private int packageY;
	private int packageZ;
	
	private int deliveryX;
	private int deliveryY;
	private int deliveryZ;

	public PacketStampPackage() {
	}

	public PacketStampPackage(BlockPos packagePos, BlockPos deliveryPos) {
		this.packageX = packagePos.getX();
		this.packageY = packagePos.getY();
		this.packageZ = packagePos.getZ();
		
		this.deliveryX = deliveryPos.getX();
		this.deliveryY = deliveryPos.getY();
		this.deliveryZ = deliveryPos.getZ();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		packageX = buf.readInt();
		packageY = buf.readInt();
		packageZ = buf.readInt();
		
		deliveryX = buf.readInt();
		deliveryY = buf.readInt();
		deliveryZ = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(packageX);
		buf.writeInt(packageY);
		buf.writeInt(packageZ);
		
		buf.writeInt(deliveryX);
		buf.writeInt(deliveryY);
		buf.writeInt(deliveryZ);
	}

	public static class Handler implements IMessageHandler<PacketStampPackage, IMessage> {
		@Override
		public IMessage onMessage(PacketStampPackage packet, MessageContext ctx) {
			BlockPackage.stampPackage(ctx.getServerHandler().player.world, new BlockPos(packet.packageX, packet.packageY, packet.packageZ), new BlockPos(packet.deliveryX, packet.deliveryY, packet.deliveryZ));

			return null;
		}
	}

}
