package com.chaosthedude.endermail.data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

public class LockerWorldData extends WorldSavedData implements Supplier<LockerWorldData> {

	public Map<String, BlockPos> lockers;

	public static final String ID = "lockers";

	public LockerWorldData() {
		this(ID);
	}

	public LockerWorldData(String name) {
		super(name);
		lockers = new HashMap<String, BlockPos>();
	}

	@Override
	public void read(CompoundNBT compound) {
		lockers = new HashMap<String, BlockPos>();
		ListNBT lockerList = compound.getList("Lockers", 10);
		for (int i = 0; i < lockerList.size(); i++) {
			CompoundNBT lockerTag = (CompoundNBT) lockerList.get(i);
			String lockerID = lockerTag.getString("ID");
			BlockPos pos = new BlockPos(lockerTag.getInt("X"), lockerTag.getInt("Y"), lockerTag.getInt("Z"));
			lockers.put(lockerID, pos);
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		ListNBT lockerList = new ListNBT();
		for (String lockerID : lockers.keySet()) {
			CompoundNBT lockerTag = new CompoundNBT();
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
		while (lockers.containsKey(lockerID)) {
			lockerID += "-";
		}
		lockers.put(lockerID, pos);
		markDirty();
		System.out.println("After adding:");
		for (String id : lockers.keySet()) {
			System.out.println(id + ", " + lockers.get(id));
		}
		return lockerID;
	}
	
	public void removeLocker(String lockerID) {
		lockers.remove(lockerID);
		System.out.println("After removing:");
		for (String id : lockers.keySet()) {
			System.out.println(id + ", " + lockers.get(id));
		}
		markDirty();
	}
	
	public Map<String, BlockPos> getLockers() {
		return lockers;
	}

	@Override
	public LockerWorldData get() {
		return this;
	}
	
	@Nullable
	public static LockerWorldData get(World world) {
		if (!world.isRemote()) {
			return get(world.getServer().getWorld(world.getDimension().getType()));
		}
		return null;
	}
	
	public static LockerWorldData get(ServerWorld world) {
		DimensionSavedDataManager data = world.getSavedData();
		LockerWorldData saver = (LockerWorldData) data.getOrCreate(() -> {
			return new LockerWorldData();
		}, ID);

		if (saver == null) {
			saver = new LockerWorldData();
			data.set(saver);
		}
		return saver;
	}

}
