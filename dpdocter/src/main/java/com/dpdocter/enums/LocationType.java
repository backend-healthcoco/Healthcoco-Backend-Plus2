package com.dpdocter.enums;

public enum LocationType {

	CLINIC("CLINIC"), LAB("LAB"), CLINICLAB("CLINICLAB"), GYM("GYM"), PHARMACY("PHARMACY"), SALOON("SALOON");

	public String type;

	public String getType() {
		return type;
	}

	private LocationType(String type) {
		this.type = type;
	}

}
