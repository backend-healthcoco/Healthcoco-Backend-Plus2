package com.dpdocter.enums;

public enum NutrientCategaoryEnum {
	GENERAL("GENERAL"), CARBOHYDRATE("CABOHYDRATE"), VITAMINS("VITAMINS"), LIPIDS("LIPIDS"),
	PROTEIN_AMINOACIDS("PROTEIN_AMINOACIDS"), MINERALS("MINERALS"), OTHERS("OTHERS");

	private String type;

	public String getType() {
		return type;
	}

	private NutrientCategaoryEnum(String type) {
		this.type = type;
	}
}
