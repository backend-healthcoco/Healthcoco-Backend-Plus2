package com.dpdocter.enums;

public enum UniqueIdInitial {

	APPOINTMENT("H"), PRESCRIPTION("P"), REPORTS("R"), CLINICALNOTES("C"), VISITS("V"), USER("USR"), HOSPITAL(
			"HOS"), LOCATION("LOC"), ISSUETRACK(
					"HCI"), FEEDBACK("F"), TREATMENT("T"), USERREPORTS("UR"), PHARMACY("PHR"), PHARMACY_REQUEST("PHRQ"),

	CONSENT_FORM("CF"), DISCHARGE_SUMMARY("DS"),

	PHARMACY_RESPONSE("PHRS"), LAB_PICKUP_REQUEST("LPR"), LAB_PICKUP_SAMPLE("LPS"), COLLECTION_BOYS("CB"), ADMIT_CARD(
			"AC"),
	ORDER_DIAGNOSTIC_TEST("ODT"), OT_REPORTS("OT"), DELIVERY_REPORTS("DR"), DOCTOR_LAB_REPORTS("DLR");

	private String initial;

	private UniqueIdInitial(String initial) {
		this.initial = initial;
	}

	public String getInitial() {
		return initial;
	}
}
