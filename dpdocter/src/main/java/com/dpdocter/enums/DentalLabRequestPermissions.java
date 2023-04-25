package com.dpdocter.enums;

public enum DentalLabRequestPermissions {

	PATIENT_NAME("PATIENT_NAME"), MOBILE_NUMBER("MOBILE_NUMBER"), AGE("AGE"), CRN("CRN"), STATUS("STATUS"),
	REQUEST_ID("STATUS"), SERIAL_NUMBER("SERIAL_NUMBER"), REASON_FOR_CANCEL("REASON_FOR_CANCEL");

	private String permission;

	public String getPermission() {
		return permission;
	}

	private DentalLabRequestPermissions(String permission) {
		this.permission = permission;
	}

}
