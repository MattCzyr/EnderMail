package com.chaosthedude.endermail.network;

import java.util.List;

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

public class PacketSpawnMailman implements IMessage {
	
	private int startingX;
	private int startingY;
	private int startingZ;
	
	private int deliveryX;
	private int deliveryY;
	private int deliveryZ;

	public PacketSpawnMailman() {
	}
	
	public PacketSpawnMailman(BlockPos startingPos, BlockPos deliveryPos) {
		this.startingX = startingPos.getX();
		this.startingY = startingPos.getY();
		this.startingZ = startingPos.getZ();
		
		this.deliveryX = deliveryPos.getX();
		this.deliveryY = deliveryPos.getY();
		this.deliveryZ = deliveryPos.getZ();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		startingX = buf.readInt();
		startingY = buf.readInt();
		startingZ = buf.readInt();
		
		deliveryX = buf.readInt();
		deliveryY = buf.readInt();
		deliveryZ = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(startingX);
		buf.writeInt(startingY);
		buf.writeInt(startingZ);
		
		buf.writeInt(deliveryX);
		buf.writeInt(deliveryY);
		buf.writeInt(deliveryZ);
	}

	public static class Handler implements IMessageHandler<PacketSpawnMailman, IMessage> {
		@Override
		public IMessage onMessage(PacketSpawnMailman packet, MessageContext ctx) {
			BlockPos min = new BlockPos(packet.startingX - 64, packet.startingY - 64, packet.startingZ - 64);
			BlockPos max = new BlockPos(packet.startingX + 64, packet.startingY + 64, packet.startingZ + 64);
			/*List<EntityEnderMailman> nearbyMailmen = ctx.getServerHandler().player.world.getEntitiesWithinAABB(EntityEnderMailman.class, new AxisAlignedBB(min, max));
			EntityEnderMailman nearby = null;
			for (EntityEnderMailman mailman : nearbyMailmen) {
				if (!mailman.isDelivering()) {
					nearby = mailman;
					break;
				}
			}*/
			
			BlockPos startingPos = new BlockPos(packet.startingX, packet.startingY, packet.startingZ);
			BlockPos deliveryPos = new BlockPos(packet.deliveryX, packet.deliveryY, packet.deliveryZ);
			
			//if (nearby != null) {
			//	nearby.setStartingPos(startingPos);
			//	nearby.setDeliveryPos(deliveryPos);
			//	nearby.teleportToStartingPos();
			//} else {
				ItemStack stack = ItemUtils.getHeldItem(ctx.getServerHandler().player, EnderMailItems.packageController);
				if (stack != null) {
					EntityEnderMailman enderMailman = new EntityEnderMailman(ctx.getServerHandler().player.world, startingPos, deliveryPos, stack);
					ctx.getServerHandler().player.world.spawnEntity(enderMailman);
				}
			//}

			return null;
		}
	}

}
