package com.dpdocter.enums;

public enum AppointmentResponseType {
    SPECIALITY("SPECIALITY"), SYMPTOM("SYMPTOM"), DOCTOR("DOCTOR"), CLINIC("CLINIC"), LABTEST("LABTEST"), LAB("LAB"), SERVICE("SERVICE"),
    PHARMACY("PHARMACY"), DISEASE("DISEASE"), CONDITION("CONDITION");

    private String responseType;

    AppointmentResponseType(String responseType) {
	this.responseType = responseType;
    }

    public String getResponseType() {
	return responseType;
    }

}
