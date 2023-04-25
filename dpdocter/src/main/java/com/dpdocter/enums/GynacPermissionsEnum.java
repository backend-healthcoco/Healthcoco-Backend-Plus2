package com.dpdocter.enums;

public enum GynacPermissionsEnum {

	BIRTH_HISTORY("BIRTH_HISTORY"), PA("PA"), PV("PV"), PS("PS"), INDICATION_OF_USG("INDICATION_OF_USG"), LMP("LMP"),
	EDD("EDD");

	private String permissions;

	private GynacPermissionsEnum(String permissions) {
		this.permissions = permissions;
	}

	public String getPermissions() {
		return permissions;
	}

}
