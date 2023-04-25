package com.dpdocter.enums;

public enum PrescriptionPermissionEnum {

	MYDRUGS("MYDRUGS"), LAB("LAB"), ADVICE("ADVICE"), ALLDRUGS("ALLDRUGS"), GENERIC_DRUG("GENERIC_DRUG");

	private String permissions;

	private PrescriptionPermissionEnum(String permissions) {
		this.permissions = permissions;
	}

	public String getPermissions() {
		return permissions;
	}

}
