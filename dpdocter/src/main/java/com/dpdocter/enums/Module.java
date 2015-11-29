package com.dpdocter.enums;

public enum Module {

    CONTACT("CONTACT"), PATIENTREGISTRATION("PATIENTREGISTRATION"), CLINICALNOTES("CLINICALNOTES"), REPORTS("REPORTS"), HISTORY("HISTORY"), PRESCRIPTION(
	    "PRESCRIPTION"), CALENDER("CALENDER");

    private String module;

    private Module(String module) {
	this.module = module;
    }

    public String getModule() {
	return module;
    }
}
