package com.dpdocter.enums;

public enum HistoryPermissionEnum {

	PAST_HISTORY("PAST_HISTORY"), PRESENT_HISTORY("PRESENT_HISTORY"), FAMILY_HISTORY(
			"FAMILY_HISTORY"), MENSTRUAL_HISTORY("MENSTRUAL_HISTORY");

	private String permissions;

	private HistoryPermissionEnum(String permissions) {
		this.permissions = permissions;
	}

	public String getPermissions() {
		return permissions;
	}

}
