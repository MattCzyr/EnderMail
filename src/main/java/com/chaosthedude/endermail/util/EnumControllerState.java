package com.chaosthedude.endermail.util;

public enum EnumControllerState {
	
	DELIVERING(0),
	SUCCESS(1),
	FAILURE(2);

	private int id;

	EnumControllerState(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	public static EnumControllerState fromID(int id) {
		for (EnumControllerState state : values()) {
			if (state.getID() == id) {
				return state;
			}
		}

		return null;
	}

}
