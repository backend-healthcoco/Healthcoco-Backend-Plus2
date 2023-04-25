package com.dpdocter.enums;

public enum CardioPermissionEnum {

	ECG("ECG"), XRAY("XRAY"), ECHO("ECHO"), HOLTER("HOLTER");

	private String permissions;

	private CardioPermissionEnum(String permissions) {
		this.permissions = permissions;
	}

	public String getPermissions() {
		return permissions;
	}

}
