package com.dpdocter.enums;

public enum PreOperationCardPermissionEnum {

	 COMPLAINT("COMPLAINT"), INVESTIGATIONS("INVESTIGATIONS"), DIAGNOSIS("DIAGNOSIS"), GENERAL_EXAMINATION("GENERAL_EXAMINATION"),
	 PAST_HISTORY("PAST_HISTORY"),	TREATMENT_PLAN("TREATMENT_PLAN"),LOCAL_EXAMINATION("LOCAL_EXAMINATION"),IPD_NUMBER("IPD_NUMBER");
	

	private String permission;

	PreOperationCardPermissionEnum(String permission) {
		this.permission = permission;
	}

	public String getPermission() {
		return permission;
	}
}
