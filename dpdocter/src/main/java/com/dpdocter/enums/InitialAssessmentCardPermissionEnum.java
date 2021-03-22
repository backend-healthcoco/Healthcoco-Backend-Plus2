package com.dpdocter.enums;

public enum InitialAssessmentCardPermissionEnum {

	PRESENT_COMPLAINT("PRESENT_COMPLAINT"), OBSERVATION("OBSERVATION"),
	PROVISIONAL_DIAGNOSIS("PROVISIONAL_DIAGNOSIS"), INVESTIGATIONS("INVESTIGATIONS"),
	NOTES("NOTES"), DIAGNOSIS("DIAGNOSIS"), GENERAL_EXAMINATION("GENERAL_EXAMINATION"),
	PSYCHOLOGICAL_EXAMINATION("PSYCHOLOGICAL_EXAMINATION"), PAST_HISTORY("PAST_HISTORY"),
	GENERAL_HISTORY("GENERAL_HISTORY");
	

	private String permission;

	InitialAssessmentCardPermissionEnum(String permission) {
		this.permission = permission;
	}

	public String getPermission() {
		return permission;
	}
}
