package com.dpdocter.enums;

public enum CountFor {
	PRESCRIPTIONS("PRESCRIPTIONS"), RECORDS("RECORDS"), NOTES("NOTES"), HISTORY("HISTORY"), PATIENTVISITS(
			"PATIENTVISITS"), TREATMENTS(
					"TREATMENTS"), EYE_PRESCRIPTION("EYE_PRESCRIPTION"), DISCHARGE_SUMMARY("DISCHARGE_SUMMARY");

	private String countRequired;

	CountFor(String countRequired) {
		this.countRequired = countRequired;
	}

	public String getCountRequired() {
		return countRequired;
	}

}
