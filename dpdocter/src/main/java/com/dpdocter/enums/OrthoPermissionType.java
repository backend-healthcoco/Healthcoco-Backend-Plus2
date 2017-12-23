package com.dpdocter.enums;

public enum OrthoPermissionType {

	IMPLANT("IMPLANT"), CEMENT("CEMENT");

	private String permission;

	public String getPermission() {
		return permission;
	}

	private OrthoPermissionType(String permission) {
		this.permission = permission;
	}

}
