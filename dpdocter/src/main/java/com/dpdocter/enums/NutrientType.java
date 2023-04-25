package com.dpdocter.enums;

public enum NutrientType {
	MICRO_NUTRIENT("MICRO_NUTRIENT"), MACRO_NUTRIENT("MICRO_NUTRIENT");

	private String type;

	public String getType() {
		return type;
	}

	private NutrientType(String type) {
		this.type = type;
	}

}
