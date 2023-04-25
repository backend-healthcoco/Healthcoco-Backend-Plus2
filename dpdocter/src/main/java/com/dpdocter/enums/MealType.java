package com.dpdocter.enums;

public enum MealType {
	RECIPE("RECIPE"), INGREDIENT("INGREDIENT");

	private String type;

	public String getType() {
		return type;
	}

	private MealType(String type) {
		this.type = type;
	}
}
