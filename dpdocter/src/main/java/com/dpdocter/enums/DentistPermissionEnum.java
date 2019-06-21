package com.dpdocter.enums;

public enum DentistPermissionEnum {

	PAIN_SCALE("PAIN_SCALE");

	private String permissions;

	private DentistPermissionEnum(String permissions) {
		this.setPermissions(permissions);
	}

	public String getPermissions() {
		return permissions;
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}

}
