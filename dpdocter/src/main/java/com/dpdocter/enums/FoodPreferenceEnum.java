package com.dpdocter.enums;

public enum FoodPreferenceEnum {
	VEG_GRAIN("VEG_GRAIN"), MILK("MILK"), EGG("EGG"), FISH("FISH"), MEAT("MEAT"), SEAFOOD("SEAFOOD"), HONEY("HONEY");

	private String type;

	public String getType() {
		return type;
	}

	private FoodPreferenceEnum(String type) {
		this.type = type;
	}

}
