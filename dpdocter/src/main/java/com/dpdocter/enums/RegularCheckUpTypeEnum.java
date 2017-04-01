package com.dpdocter.enums;

public enum RegularCheckUpTypeEnum {

	VISIT("VISIT"), REGISTRATION("REGISTRATION"), NOT_ALLOWED("NOT_ALLOWED");

	private String type;

	public String getType() {
		return type;
	}

	private RegularCheckUpTypeEnum(String type) {
		this.type = type;
	}

}