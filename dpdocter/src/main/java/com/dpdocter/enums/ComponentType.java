package com.dpdocter.enums;

public enum ComponentType {

	CLINICAL_NOTES("CLINICAL_NOTES"), REPORTS("REPORTS"), PRESCRIPTIONS("PRESCRIPTIONS"), ALL("ALL"), BILLING(
			"BILLING"), VISITS("VISITS"), PATIENT("PATIENT"), DOCTOR("DOCTOR"), TREATMENT("TREATMENT"), APPOINTMENT(
					"APPOINTMENT"), CALENDAR_REMINDER("CALENDAR_REMINDER"), INVOICE(
							"INVOICE"), RECEIPT("RECEIPT"), EYE_PRESCRIPTION("EYE_PRESCRIPTION"),CONSENT_FORM("CONSENT_FORM");

	private String type;

	private ComponentType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
