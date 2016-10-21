package com.dpdocter.enums;

public enum PrescriptionPermissionEnum {

	DRUG("DRUG"), DOSAGE("DOSAGE"), DIRECTION("DIRECTION"), TEMPLATE("TEMPLATE"), LAB("LAB"), ADVICE("ADVICE");

	private String permissions;

	private PrescriptionPermissionEnum(String permissions) {
		this.permissions = permissions;
	}

	public String getPermissions() {
		return permissions;
	}

}
