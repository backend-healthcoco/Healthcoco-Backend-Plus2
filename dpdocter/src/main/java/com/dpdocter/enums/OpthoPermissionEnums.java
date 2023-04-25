package com.dpdocter.enums;

public enum OpthoPermissionEnums {

	OPTHO_RX("OPTHO_RX"), OPTHO_CLINICAL_NOTES("OPTHO_CLINICAL_NOTES"), LENS_PRESCRIPTION("LENS_PRESCRIPTION"),;

	private String permissions;

	private OpthoPermissionEnums(String permissions) {
		this.permissions = permissions;
	}

	public String getPermissions() {
		return permissions;
	}

}
