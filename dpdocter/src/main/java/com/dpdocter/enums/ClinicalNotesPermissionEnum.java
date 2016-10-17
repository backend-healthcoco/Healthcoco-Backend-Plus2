package com.dpdocter.enums;

public enum ClinicalNotesPermissionEnum {

	VITAL_SIGNS("VITAL_SIGNS"), PRESENT_COMPLAINT("PRESENT_COMPLAINT"), COMPLAINT("COMPLAINT"), OBSERVATION(
			"OBSERVATION"), WORKING_DIAGRAM("WORKING_DIAGRAM"), INVESTIGATIONS("INVESTIGATIONS"), DIAGRAM(
					"DIAGRAM"), NOTES(
							"NOTES"), DIAGNOSIS("DIAGNOSIS"), GENERAL_EXAM("GENERAL_EXAM"), SYSTEM_EXAM("SYSTEM_EXAM");

	private String permissions;

	private ClinicalNotesPermissionEnum(String permissions) {
		this.permissions = permissions;
	}

	public String getPermissions() {
		return permissions;
	}

}
