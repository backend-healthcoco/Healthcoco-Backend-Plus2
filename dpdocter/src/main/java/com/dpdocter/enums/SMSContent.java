package com.dpdocter.enums;

public enum SMSContent {

    PATIENT_NAME("PATIENT_NAME"), DOCTOR_NAME("DOCTOR_NAME"), APPOINTMENT_ID("APPOINTMENT_ID"), DATE_TIME("DATE_TIME"), CLINIC_NAME(
	    "CLINIC_NAME"), CLINIC_CONTACT_NUMBER("CLINIC_CONTACT_NUMBER"), NO_OF_APPOINTMENTS("NO_OF_APPOINTMENTS"),SLOT_SMS("SLOT_SMS"), BRANCH("BRANCH");

    private String content;

    public String getContent() {
	return content;
    }

    private SMSContent(String content) {
	this.content = content;
    }
}
