package com.dpdocter.enums;

public enum PatientVisitPermissionEnum {

	PRESCRIPTION("PRESCRIPTION"), CLINICALNOTES("CLINICALNOTES"), TREATMENT("TREATMENT");

	private String permissions;

	private PatientVisitPermissionEnum(String permissions) {
		this.permissions = permissions;
	}

	public String getPermissions() {
		return permissions;
	}

}
