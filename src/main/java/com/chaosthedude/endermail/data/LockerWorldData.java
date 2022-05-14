package com.chaosthedude.endermail.data;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class LockerWorldData extends SavedData {

	public Map<String, BlockPos> lockers;

	public static final String ID = "lockers";
	
	public LockerWorldData() {
		lockers = new HashMap<String, BlockPos>();
		setDirty();
	}

	public LockerWorldData(CompoundTag tag) {
		lockers = new HashMap<String, BlockPos>();
		ListTag lockerList = tag.getList("Lockers", 10);
		for (int i = 0; i < lockerList.size(); i++) {
			CompoundTag lockerTag = (CompoundTag) lockerList.get(i);
			String lockerID = lockerTag.getString("ID");
			BlockPos pos = new BlockPos(lockerTag.getInt("X"), lockerTag.getInt("Y"), lockerTag.getInt("Z"));
			lockers.put(lockerID, pos);
		}
	}

	@Override
	public CompoundTag save(CompoundTag compound) {
		ListTag lockerList = new ListTag();
		for (String lockerID : lockers.keySet()) {
			CompoundTag lockerTag = new CompoundTag();
			lockerTag.putString("ID", lockerID);
			lockerTag.putInt("X", lockers.get(lockerID).getX());
			lockerTag.putInt("Y", lockers.get(lockerID).getY());
			lockerTag.putInt("Z", lockers.get(lockerID).getZ());
			lockerList.add(lockerTag);
		}
		compound.put("Lockers", lockerList);
		return compound;
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
	
	public static LockerWorldData get(ServerLevel world) {
		DimensionDataStorage data = world.getDataStorage();
		LockerWorldData saver = (LockerWorldData) data.computeIfAbsent((tag) -> {
			return new LockerWorldData(tag);
		}, () -> {
			return new LockerWorldData();
		}, ID);
		return saver;
	}

}
