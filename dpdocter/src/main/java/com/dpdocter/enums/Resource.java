package com.dpdocter.enums;

public enum Resource {
    COMPLAINT("COMPLAINT"), OBSERVATION("OBSERVATION"), INVESTIGATION("INVESTIGATION"), DIAGNOSIS("DIAGNOSIS"), NOTES("NOTES"), DIAGRAM("DIAGRAM"), PATIENT(
	    "PATIENT"), DRUG("DRUG"), LABTEST("LABTEST"), COUNTRY("COUNTRY"), CITY("CITY"),LANDMARKLOCALITY("LANDMARKLOCALITY"),DOCTOR("DOCTOR");

    private String type;

    private Resource(String type) {
	this.type = type;
    }

    public String getType() {
	return type;
    }

}
