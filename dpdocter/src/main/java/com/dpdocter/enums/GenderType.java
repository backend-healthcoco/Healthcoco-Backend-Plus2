package com.dpdocter.enums;

public enum GenderType {

	MALE("MALE"), FEMALE("FEMALE");

	private String type;

	private GenderType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
