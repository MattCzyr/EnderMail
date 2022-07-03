package com.chaosthedude.endermail.data;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class LockerData extends SavedData {
	
	public static final String ID = "lockers";

	private final Map<String, BlockPos> lockers = new HashMap<String, BlockPos>();
	
	public LockerData() {
	}

	public LockerData(CompoundTag tag) {
		ListTag lockerList = tag.getList("Lockers", Tag.TAG_COMPOUND);
		for (Tag t : lockerList) {
			CompoundTag lockerTag = (CompoundTag) t;
			String lockerID = lockerTag.getString("ID");
			BlockPos pos = new BlockPos(lockerTag.getInt("X"), lockerTag.getInt("Y"), lockerTag.getInt("Z"));
			lockers.put(lockerID, pos);
		}
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		ListTag lockerList = new ListTag();
		for (String lockerID : lockers.keySet()) {
			CompoundTag lockerTag = new CompoundTag();
			lockerTag.putString("ID", lockerID);
			lockerTag.putInt("X", lockers.get(lockerID).getX());
			lockerTag.putInt("Y", lockers.get(lockerID).getY());
			lockerTag.putInt("Z", lockers.get(lockerID).getZ());
			lockerList.add(lockerTag);
		}
		tag.put("Lockers", lockerList);
		return tag;
	}
	
	public String createLocker(String lockerID, BlockPos pos) {
		int suffixIndex = 2;
		String fixedLockerID = lockerID;
		while (lockerExists(fixedLockerID) && fixedLockerID.length() < 12) {
			fixedLockerID = lockerID + suffixIndex;
			suffixIndex++;
		}
		if (fixedLockerID.length() >= 12) {
			return "";
		}
		lockers.put(fixedLockerID, pos);
		setDirty();
		return fixedLockerID;
	}
	
	public boolean lockerExists(String lockerID) {
		return lockers.containsKey(lockerID);
	}
	
	public void removeLocker(String lockerID) {
		lockers.remove(lockerID);
		setDirty();
	}
	
	public Map<String, BlockPos> getLockers() {
		return lockers;
	}
	
	public static LockerData get(ServerLevel level) {
		DimensionDataStorage data = level.getDataStorage();
		return (LockerData) data.computeIfAbsent(LockerData::new, LockerData::new, ID);
	}

}
