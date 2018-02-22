package com.dpdocter.enums;

public enum PatientCertificatePermissions {

	MEDICAL_CERTIFICATE("MEDICAL_CERTIFICATE"), FITNESS_CERTIFICATE("FITNESS_CERTIFICATE"), UNFIT_CERTIFICATE("UNFIT_CERTIFICATE"), 
	SPECICAL_INFORMATION_CONSENT("SPECICAL_INFORMATION_CONSENT"), 
	HIGH_RISK_CONSENT_FORM("HIGH_RISK_CONSENT_FORM"), CONSENT_FOR_BLOOD_TRANFUSION("CONSENT_FOR_BLOOD_TRANFUSION"), MLC_INFORMATION("MLC_INFORMATION");

	private String permission;

	public String getPermission() {
		return permission;
	}

	private PatientCertificatePermissions(String permission) {
		this.permission = permission;
	}

}
