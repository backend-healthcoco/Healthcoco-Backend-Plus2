package com.dpdocter.enums;

public enum UnitType {
	PERCENT("%"), INR("₹");

	private String unit;

	private UnitType(String unit) {
		this.unit = unit;
	}

	public String getUnit() {
		return unit;
	}

}
