package com.chaosthedude.endermail.util;

public enum EnumControllerState {
	
	DEFAULT(0),
	DELIVERING(1),
	SUCCESS(2),
	FAILURE(3);

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
