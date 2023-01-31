package com.dpdocter.enums;

public enum InitialAssessmentCardPermissionEnum {

	PRESENT_COMPLAINT("PRESENT_COMPLAINT"), OBSERVATION("OBSERVATION"),
	PROVISIONAL_DIAGNOSIS("PROVISIONAL_DIAGNOSIS"), INVESTIGATIONS("INVESTIGATIONS"),
	GENERAL_EXAMINATION("GENERAL_EXAMINATION"),
	PSYCHOLOGICAL_ASSESSMENT("PSYCHOLOGICAL_ASSESSMENT"), PAST_HISTORY("PAST_HISTORY"),
	IPD_NUMBER("IPD_NUMBER"),TREATMENT_PLAN("TREATMENT_PLAN");

	private String permission;

	InitialAssessmentCardPermissionEnum(String permission) {
		this.permission = permission;
	}

	public String getPermission() {
		return permission;
	}
}
