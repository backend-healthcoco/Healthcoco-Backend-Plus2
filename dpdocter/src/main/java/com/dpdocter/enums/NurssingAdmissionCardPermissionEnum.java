package com.dpdocter.enums;

public enum NurssingAdmissionCardPermissionEnum {

	OLD_MEDICATION("OLD_MEDICATION"), NURSING_CARE("NURSING_CARE"), RISK_FACTOR("XRAY"),
	CO_MORBIDITIES("CO_MORBIDITIES");
	private String permission;

	NurssingAdmissionCardPermissionEnum(String permission) {
		this.permission = permission;
	}

	public String getPermission() {
		return permission;
	}

}
