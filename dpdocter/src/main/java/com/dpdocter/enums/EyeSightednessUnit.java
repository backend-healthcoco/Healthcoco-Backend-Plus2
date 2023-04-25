package com.dpdocter.enums;

public enum EyeSightednessUnit {

	NEAR("-"), FAR("+");

	private String type;

	private EyeSightednessUnit(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
