package com.dpdocter.enums;

public enum ComponentType {

    CLINICAL_NOTES("CLINICAL_NOTES"), REPORTS("REPORTS"), PRESCRIPTIONS("PRESCRIPTIONS"), ALL("ALL"),
    BILING("BILING"), VISITS("VISITS"), PATIENT("PATIENT"), DOCTOR("DOCTOR"),
    TREATMENT("TREATMENT"), APPOINTMENT("APPOINTMENT");

    private String type;

    private ComponentType(String type) {
	this.type = type;
    }

    public String getType() {
	return type;
    }

}
