package com.dpdocter.enums;

public enum RoleEnum {
	ADMIN_DOCTER("ADMIN_DOCTER"),DOCTER("DOCTER"),PATIENT("PATIENT");
	
	private String role;
	
	private RoleEnum(String role){
		this.role = role;
	}

	public String getRole() {
		return role;
	}
	
	
}
