package com.dpdocter.enums;

public enum AdmitCardPermissionEnum {
	NATURE_OF_OPERATION(" NATURE_OF_OPERATION"),JOINT_INVOLVEMENT("JOINT_INVOLVEMENT"),XRAY("XRAY");
	private String permission;

	public String getPermission() {
		return permission;
	}

	AdmitCardPermissionEnum(String permission) {
		this.permission = permission;
	}

}
