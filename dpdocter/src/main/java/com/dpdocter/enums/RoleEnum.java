package com.dpdocter.enums;

public enum RoleEnum {
	SUPER_ADMIN("SUPER_ADMIN"), HOSPITAL_ADMIN("HOSPITAL_ADMIN"), LOCATION_ADMIN("LOCATION_ADMIN"), DOCTOR("DOCTOR"), PATIENT("PATIENT");

	private String role;

	private RoleEnum(String role) {
		this.role = role;
	}

	public String getRole() {
		return role;
	}

}
