package com.dpdocter.enums;

public enum Resource {
    COMPLAINT("COMPLAINT"), OBSERVATION("OBSERVATION"), INVESTIGATION("INVESTIGATION"), DIAGNOSIS("DIAGNOSIS"), NOTES("NOTES"), DIAGRAM("DIAGRAM"), PATIENT(
	    "PATIENT"), DRUG("DRUG"), LABTEST("LABTEST"), COUNTRY("COUNTRY"), STATE("STATE"), CITY("CITY"), LANDMARKLOCALITY("LANDMARKLOCALITY"), DOCTOR("DOCTOR"),
    LOCATION("LOCATION");

    private String type;

    private Resource(String type) {
	this.type = type;
    }

    public String getType() {
	return type;
    }

}
