package com.chaosthedude.endermail.util;

import net.minecraft.util.math.BlockPos;

public class StampInfo {
	
	public BlockPos deliveryPos;
	public String lockerID;
	
	public StampInfo(BlockPos deliveryPos, String lockerID) {
		this.deliveryPos = deliveryPos;
		this.lockerID = lockerID;
	}

}
