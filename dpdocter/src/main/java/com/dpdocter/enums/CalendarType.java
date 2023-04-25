package com.dpdocter.enums;

public enum CalendarType {

	DAILY("DAILY"), MONTHLY("MONTHLY"), WEEKLY("WEEKLY"), LIST("LIST");

	private String type;

	private CalendarType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
