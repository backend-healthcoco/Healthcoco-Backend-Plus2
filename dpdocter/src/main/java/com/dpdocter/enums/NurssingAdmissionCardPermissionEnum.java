package com.dpdocter.enums;

public enum NurssingAdmissionCardPermissionEnum {

	OLD_MEDICATION("OLD_MEDICATION"), NURSING_CARE("NURSING_CARE"), RISK_FACTOR("RISK_FACTOR"),
	CO_MORBIDITIES("CO_MORBIDITIES"), IPD_NUMBER("IPD_NUMBER");

	private String permission;

	NurssingAdmissionCardPermissionEnum(String permission) {
		this.permission = permission;
	}

	public String getPermission() {
		return permission;
	}

}
