package com.dpdocter.enums;

public enum FitnessGoal {

	BEGINNER("BEGINNER"), INTERMEDIATE("INTERMEDIATE"), ADVANCE("ADVANCE");

	private String type;

	public String getType() {
		return type;
	}

	private FitnessGoal(String type) {
		this.type = type;
	}

}
