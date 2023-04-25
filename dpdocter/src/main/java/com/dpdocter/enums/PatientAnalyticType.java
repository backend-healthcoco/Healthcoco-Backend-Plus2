package com.dpdocter.enums;

public enum PatientAnalyticType {

	NEW_PATIENT("NEW_PATIENT"), VISITED_PATIENT("VISITED_PATIENT"), CITY_WISE("CITY_WISE"),
	LOCALITY_WISE("LOCALITY_WISE"), TOP_10_VISITED("TOP_10_VISITED"), IN_GROUP("IN_GROUP");

	private String analyticType;

	public String getAnalyticType() {
		return analyticType;
	}

	private PatientAnalyticType(String analyticType) {
		this.analyticType = analyticType;
	}

}
