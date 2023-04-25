package com.dpdocter.enums;

public enum GestationType {

	FULL_TERM("FULL_TERM"), PREMATURE("PREMATURE");

	private String type;

	private GestationType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
