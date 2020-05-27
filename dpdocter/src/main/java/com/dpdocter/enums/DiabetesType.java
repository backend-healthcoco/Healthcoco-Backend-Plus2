package com.dpdocter.enums;

public enum DiabetesType {
	DIABETES_TYPE_I("DIABETES_TYPE_I"), DIABETES_TYPE_II("DIABETES_TYPE_II");
	private String type;

	public String getType() {
		return type;
	}

	private DiabetesType(String type) {
		this.type = type;
	}
}
