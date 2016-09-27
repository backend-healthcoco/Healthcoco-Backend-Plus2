package com.dpdocter.enums;

public enum AnaesthesiaTypeEnum {

	GA("GA"), SA("SA"), LA("LA"), EA("EA"), EMPTY("EMPTY");

	private String anaesthesiaType;

	public String getAnaesthesiaType() {
		return anaesthesiaType;
	}

	private AnaesthesiaTypeEnum(String anaesthesiaType) {
		this.anaesthesiaType = anaesthesiaType;
	}

}
