package com.dpdocter.enums;

public enum FluctuateWeightType {

	GAIN_WEIGHT("GAIN_WEIGHT"), LOSE_WEIGHT("LOSE_WEIGHT");

	private String type;

	public String getType() {
		return type;
	}

	private FluctuateWeightType(String type) {
		this.type = type;
	}
}
