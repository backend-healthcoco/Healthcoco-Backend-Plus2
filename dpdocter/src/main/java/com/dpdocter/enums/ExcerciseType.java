package com.dpdocter.enums;

public enum ExcerciseType {
	GYM("GYM"), YOGA("YOGA"), WALKING("WALKING"), RUNNING("RUNNING"), BRISK_WALK("BRISK_WALK"), CYCLING("CYCLING"),
	WEIGHT_LIFTING("WEIGHT_LIFTING"), SWIMMING("SWIMMING"), OTHER("OTHER");
	private String type;

	public String getType() {
		return type;
	}

	private ExcerciseType(String type) {
		this.type = type;
	}

}
