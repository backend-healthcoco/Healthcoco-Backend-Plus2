package com.dpdocter.enums;

public enum DishType {
	VEG("VEG"), NON_VEG("NON_VEG"), EGG("EGG"), VEGAN("VEGAN");

	private String type;

	public String getType() {
		return type;
	}

	private DishType(String type) {
		this.type = type;
	}

}
