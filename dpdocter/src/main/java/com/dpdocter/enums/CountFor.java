package com.dpdocter.enums;

public enum CountFor {
    PRESCRIPTIONS("PRESCRIPTIONS"), RECORDS("RECORDS"), NOTES("NOTES"), HISTORY("HISTORY"), PATIENTVISITS("PATIENTVISITS"),TREATMENTS("TREATMENTS");

    private String countRequired;

    CountFor(String countRequired) {
	this.countRequired = countRequired;
    }

    public String getCountRequired() {
	return countRequired;
    }

}
