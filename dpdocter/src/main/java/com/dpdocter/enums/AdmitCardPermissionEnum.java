package com.dpdocter.enums;

public enum AdmitCardPermissionEnum {
	NATURE_OF_OPERATION(" NATURE_OF_OPERATION"), JOINT_INVOLVEMENT("JOINT_INVOLVEMENT"), XRAY("XRAY"), EXAMINATION(
			"EXAMINATION"), DIAGNOSIS("DIAGNOSIS"), TREATMENTPLAN("TREATMENTPLAN"),COMPLAINT("COMPLAINT"),PERSONAL_HISTORY("PERSONAL_HISTORY"),PAST_HISTORY("PAST_HISTORY"),FAMILY_HISTORY("FAMILY_HISTORY");
	private String permission;

	public String getPermission() {
		return permission;
	}

	AdmitCardPermissionEnum(String permission) {
		this.permission = permission;
	}

}
