package com.dpdocter.enums;

public enum FeedbackType {

	HELP_US("HELP_US"), REFERRER("REFERRER"), PRESCRIPTION("PRESCRIPTION"), APPOINTMENT("APPOINTMENT"),
	DOCTOR("DOCTOR"), LAB("LAB"), RECOMMENDATION("RECOMMENDATION"), REPORT("REPORT"), PHARMACY("PHARMACY");

	private String type;

	private FeedbackType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
