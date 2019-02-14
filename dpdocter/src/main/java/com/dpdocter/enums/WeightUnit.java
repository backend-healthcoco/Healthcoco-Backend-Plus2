package com.dpdocter.enums;

public enum WeightUnit {
	KG("KG"), GRAM("GRAM"), LBS("LBS"), ST("ST");

	private String unit;

	private WeightUnit(String unit) {
		this.unit = unit;
	}

	public String getUnit() {
		return unit;
	}
}
