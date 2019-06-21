package com.dpdocter.enums;

public enum LifeStyleType {

	SENDETARY("SENDETARY"), MODERATE("MODERATE"), HEAVY("HEAVY");

	private String type;

	public String getType() {
		return type;
	}

	private LifeStyleType(String type) {
		this.type = type;
	}

}
