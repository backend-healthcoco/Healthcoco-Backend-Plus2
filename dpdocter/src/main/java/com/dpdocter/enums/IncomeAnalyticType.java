package com.dpdocter.enums;

public enum IncomeAnalyticType {

	DOCTOR("DOCTOR"), CLINIC("CLINIC"), GROUP("GROUP"), PROCEDURE("PROCEDURE");

	private String analyticType;

	public String getAnalyticType() {
		return analyticType;
	}

	private IncomeAnalyticType(String analyticType) {
		this.analyticType = analyticType;
	}

}
