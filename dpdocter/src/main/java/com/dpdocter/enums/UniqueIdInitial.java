package com.dpdocter.enums;

public enum UniqueIdInitial {

    APPOINTMENT("H"), PRESCRIPTION("P"), REPORTS("R"), CLINICALNOTES("C"), VISITS("V"), USER("USR"), HOSPITAL("HOS"), LOCATION("LOC"), ISSUETRACK("HLTISH"), FEEDBACK("F");

    private String initial;

    private UniqueIdInitial(String initial) {
	this.initial = initial;
    }

    public String getInitial() {
	return initial;
    }
}
