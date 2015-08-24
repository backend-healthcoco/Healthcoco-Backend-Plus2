package com.dpdocter.solr.enums;

public enum AppointmentResponseType {
    SPECIALITY("SPECIALITY"), SYMPTOM("SYMPTOM"), DOCTOR("DOCTOR"), CLINIC("CLINIC");

    private String responseType;

    AppointmentResponseType(String responseType) {
	this.responseType = responseType;
    }

    public String getResponseType() {
	return responseType;
    }

}
