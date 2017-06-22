package com.dpdocter.enums;

public enum DentistPermissionEnum {
	PROCEDURE_NOTE("PROCEDURE_NOTE");
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
