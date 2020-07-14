package com.dpdocter.enums;

public enum DoctorFacility {

    IBS("IBS"), CALL("CALL"), BOOK("BOOK"),VIEW("VIEW");

    private String type;

    private DoctorFacility(String type) {
	this.type = type;
    }

    public String getType() {
	return type;
    }
}
