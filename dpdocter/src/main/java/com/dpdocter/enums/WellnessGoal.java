package com.dpdocter.enums;

public enum WellnessGoal {

	LOSE_WEIGHT("LOSE_WEIGHT"), GAIN_WEIGHT("GAIN_WEIGHT"), DISEASE_MANAGEMENT("DISEASE_MANAGEMENT"), OTHER("OTHER"),
	NA("NA");

	private String type;

	private WellnessGoal(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
