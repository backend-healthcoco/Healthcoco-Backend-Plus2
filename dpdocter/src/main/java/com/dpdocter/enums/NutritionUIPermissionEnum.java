package com.dpdocter.enums;

public enum NutritionUIPermissionEnum {

	ASSISSMENT_FORM("ASSISSMENT_FORM"), PATIENTLIST("PATIENT_LIST"), DIET_PLAN("DIET_PLAN");

	private String permission;

	private NutritionUIPermissionEnum(String permission) {
		this.permission = permission;
	}

	public String getPermission() {
		return permission;
	}

}
