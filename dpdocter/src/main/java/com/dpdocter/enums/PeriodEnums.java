package com.dpdocter.enums;

public enum PeriodEnums {

SEVEN_DAYS("SEVEN_DAYS"),FIFTEEN_DAYS("FIFTEEN_DAYS"),THIRTY_DAYS("THIRTY_DAYS"),THREE_MONTHS("THREE_MONTHS"),SIX_MONTHS("SIX_MONTHS"),ONE_YEAR("ONE_YEAR");
	
	private String type;

	private PeriodEnums (String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
