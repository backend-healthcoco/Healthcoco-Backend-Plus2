package com.dpdocter.enums;

public enum AccessPermissionType {
	READ("READ"), WRITE("WRITE"), DELETE("DELETE"), HIDE("HIDE");

	private String accessPermissionType;

	private AccessPermissionType(String accessPermissionType) {
		this.accessPermissionType = accessPermissionType;
	}

	public String getAccessPermissionType() {
		return accessPermissionType;
	}

}
