package com.dpdocter.enums;

public enum BloodPressureType {
	HIGH_BP("HIGH_BP"), LOW_BP("LOW_BP");

	private String type;

	public String getType() {
		return type;
	}

	private BloodPressureType(String type) {
		this.type = type;
	}
}
