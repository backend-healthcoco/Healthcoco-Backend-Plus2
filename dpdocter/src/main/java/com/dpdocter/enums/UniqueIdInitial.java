package com.dpdocter.enums;

public enum UniqueIdInitial {

	APPOINTMENT("H"), PRESCRIPTION("P"), REPORTS("R"), CLINICALNOTES("C"), VISITS("V"), USER("USR"), HOSPITAL(
			"HOS"), LOCATION("LOC"), ISSUETRACK("HCI"), FEEDBACK("F"), TREATMENT("T"), USERREPORTS("UR"), PHARMACY("PHR"),  PHARMACY_REQUEST("PHRQ"),
	PHARMACY_RESPONSE("PHRS");

	private String initial;

	private UniqueIdInitial(String initial) {
		this.initial = initial;
	}

	public String getInitial() {
		return initial;
	}
}
