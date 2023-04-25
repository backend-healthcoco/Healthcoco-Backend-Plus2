package com.dpdocter.enums;

public enum GoalStatus {

	REFERRED("REFERRED"), ON_HOLD("ON_HOLD"), ADOPTED("ADOPTED"), COMPLETED("COMPLETED"), REJECTED("REJECTED"),
	MET_GOALS("MET_GOALS");

	private String type;

	public String getType() {
		return type;
	}

	private GoalStatus(String type) {
		this.type = type;
	}

}
