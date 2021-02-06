package com.chaosthedude.endermail.network;

import java.util.function.Supplier;

import com.chaosthedude.endermail.blocks.LockerBlock;
import com.chaosthedude.endermail.blocks.PackageBlock;
import com.chaosthedude.endermail.registry.EnderMailItems;
import com.chaosthedude.endermail.util.ItemUtils;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class StampPackagePacket {

	private int packageX;
	private int packageY;
	private int packageZ;

	private int deliveryX;
	private int deliveryY;
	private int deliveryZ;
	
	private String lockerID;
	
	private boolean hasDeliveryPos;

	public StampPackagePacket() {
	}

	public StampPackagePacket(BlockPos packagePos, BlockPos deliveryPos, String lockerID, boolean hasDeliveryPos) {
		this.packageX = packagePos.getX();
		this.packageY = packagePos.getY();
		this.packageZ = packagePos.getZ();

		this.deliveryX = deliveryPos.getX();
		this.deliveryY = deliveryPos.getY();
		this.deliveryZ = deliveryPos.getZ();
		
		this.lockerID = lockerID;
		
		this.hasDeliveryPos = hasDeliveryPos;
	}

	public StampPackagePacket(PacketBuffer buf) {
		packageX = buf.readInt();
		packageY = buf.readInt();
		packageZ = buf.readInt();

		deliveryX = buf.readInt();
		deliveryY = buf.readInt();
		deliveryZ = buf.readInt();
		
		lockerID = buf.readString(LockerBlock.MAX_ID_LENGTH);
		
		hasDeliveryPos = buf.readBoolean();
	}

	public void toBytes(PacketBuffer buf) {
		buf.writeInt(packageX);
		buf.writeInt(packageY);
		buf.writeInt(packageZ);

		buf.writeInt(deliveryX);
		buf.writeInt(deliveryY);
		buf.writeInt(deliveryZ);
		
		buf.writeString(lockerID);
		
		buf.writeBoolean(hasDeliveryPos);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			PackageBlock.stampPackage(ctx.get().getSender().world, new BlockPos(packageX, packageY, packageZ), new BlockPos(deliveryX, deliveryY, deliveryZ), lockerID, hasDeliveryPos);
			ItemStack stampStack = ItemUtils.getHeldItem(ctx.get().getSender(), EnderMailItems.STAMP);
			if (stampStack != null && !ctx.get().getSender().isCreative()) {
				stampStack.setCount(stampStack.getCount() - 1);
			}
		});
		ctx.get().setPacketHandled(true);
	}

}
