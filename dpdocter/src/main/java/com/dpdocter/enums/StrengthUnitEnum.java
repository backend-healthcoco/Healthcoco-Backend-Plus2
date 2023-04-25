package com.dpdocter.enums;

public enum StrengthUnitEnum {
	MG("MG"), GM("GM");

	private String strengthUnit;

	StrengthUnitEnum(String strengthUnit) {
		this.strengthUnit = strengthUnit;
	}

	public String getStrengthUnit() {
		return strengthUnit;
	}

}
