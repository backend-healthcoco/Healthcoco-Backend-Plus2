package com.dpdocter.enums;

public enum DischargeSummaryPermissions {

	LABOUR_NOTES("LABOUR_NOTES"), BABY_NOTES("BABY_NOTES"), BIRTH_WEIGHT("BIRTH_WEIGHT"), OPERATIONAL_NOTES(
			"OPERATIONAL_NOTES"), CONDITIONS_AT_DISCHARGE("CONDITIONS_AT_DISCHARGE"), TREATMENT_GIVEN(
					"TREATMENT_GIVEN"), SUMMARY("SUMMARY");

	private String permission;

	public String getPermission() {
		return permission;
	}

	private DischargeSummaryPermissions(String permission) {
		this.permission = permission;
	}

}
