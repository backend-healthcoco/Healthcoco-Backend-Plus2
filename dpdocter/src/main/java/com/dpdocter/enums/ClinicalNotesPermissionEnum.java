package com.dpdocter.enums;

public enum ClinicalNotesPermissionEnum {

	VITAL_SIGNS("VITAL_SIGNS"), PRESENT_COMPLAINT("PRESENT_COMPLAINT"), COMPLAINT("COMPLAINT"), OBSERVATION(
			"OBSERVATION"), PROVISIONAL_DIAGNOSIS("PROVISIONAL_DIAGNOSIS"), INVESTIGATIONS("INVESTIGATIONS"), DIAGRAM(
					"DIAGRAM"), NOTES("NOTES"), DIAGNOSIS("DIAGNOSIS"), GENERAL_EXAMINATION(
							"GENERAL_EXAMINATION"), SYSTEM_EXAMINATION(
									"SYSTEM_EXAMINATION"), HISTORY_OF_PRESENT_COMPLAINT(
											"HISTORY_OF_PRESENT_COMPLAINT"), MENSTRUAL_HISTORY("MENSTRUAL_HISTORY");

	private String permissions;

	private ClinicalNotesPermissionEnum(String permissions) {
		this.permissions = permissions;
	}

	public String getPermissions() {
		return permissions;
	}

}
