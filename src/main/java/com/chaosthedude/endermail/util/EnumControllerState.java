package com.chaosthedude.endermail.util;

public enum EnumControllerState {
	
	DEFAULT(0),
	DELIVERING(1),
	DELIVERED(2),
	RETURNED(3),
	TOOFAR(4);

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
