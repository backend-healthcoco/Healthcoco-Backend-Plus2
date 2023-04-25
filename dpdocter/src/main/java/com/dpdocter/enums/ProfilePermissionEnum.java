package com.dpdocter.enums;

public enum ProfilePermissionEnum {

	PAST_HISTORY("PAST_HISTORY"), PRESENT_HISTORY("PRESENT_HISTORY"), FAMILY_HISTORY("FAMILY_HISTORY"),
	DRUG_AND_ALLERGIES("DRUG_AND_ALLERGIES"), PERSONAL_HISTORY("PERSONAL_HISTORY");

	private String permissions;

	private ProfilePermissionEnum(String permissions) {
		this.permissions = permissions;
	}

	public String getPermissions() {
		return permissions;
	}

}
