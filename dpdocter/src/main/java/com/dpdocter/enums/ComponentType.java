package com.dpdocter.enums;

public enum ComponentType {

    CLINICAL_NOTES("CLINICAL_NOTES"), REPORTS("REPORTS"), PRESCRIPTIONS("PRESCRIPTIONS"), ALL("ALL"), BILING("BILING");

    private String type;

    private ComponentType(String type) {
	this.type = type;
    }

    public String getType() {
	return type;
    }

}
