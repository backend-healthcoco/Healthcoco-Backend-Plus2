package com.dpdocter.enums;

public enum TabPermissionsEnum {

	PRESCRIPTION("PRESCRIPTION"),CLINICAL_NOTES("CLINICAL_NOTES"),HISTORY("HISTORY"),TREATMENT("TREATMENT"),REPORTS("REPORTS"),BILLING("BILLING");
	
	private String permissions;

	private TabPermissionsEnum(String permissions) {
		this.permissions = permissions;
	}

	public String getPermissions() {
		return permissions;
	}
	
}
