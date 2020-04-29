package com.chaosthedude.endermail.util;

public enum ControllerState {

	DEFAULT(0),
	DELIVERING(1),
	DELIVERED(2),
	DELIVERED_TO_LOCKER(3),
	UNDELIVERABLE(4),
	TOOFAR(5);

	private int id;

	ControllerState(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	public static ControllerState fromID(int id) {
		for (ControllerState state : values()) {
			if (state.getID() == id) {
				return state;
			}
		}

		return null;
	}

}
