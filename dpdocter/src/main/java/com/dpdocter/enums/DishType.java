package com.dpdocter.enums;

public enum DishType {
	VEG("VEG"), NONVEG("NONVEG"),EGG("EGG");
	private String type;

	public String getType() {
		return type;
	}

	private DishType(String type) {
		this.type = type;
	}

}
