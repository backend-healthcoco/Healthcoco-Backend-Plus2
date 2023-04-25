package com.dpdocter.enums;

public enum Day {
	MONDAY("MONDAY"), TUESDAY("TUESDAY"), WEDNESDAY("WEDNESDAY"), THURSDAY("THURSDAY"), FRIDAY("FRIDAY"),
	SATURDAY("SATURDAY"), SUNDAY("SUNDAY");

	private String day;

	private Day(String day) {
		this.day = day;
	}

	public String getDay() {
		return day;
	}

}
