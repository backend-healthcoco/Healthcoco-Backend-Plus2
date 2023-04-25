package com.dpdocter.enums;

public enum KioskDynamicUiEnum {
	PATIENT_REGISTER("PATIENT_REGISTER"), VIDEO("VIDEO"), FEEDBACK("FEEDBACK"), DOCTOR_AND_CLINIC("DOCTOR_AND_CLINIC"),
	BLOGS("BLOGS");

	private String uiPermission;

	private KioskDynamicUiEnum(String uiPermission) {
		this.uiPermission = uiPermission;
	}

	public String getUiPermission() {
		return uiPermission;
	}

}
