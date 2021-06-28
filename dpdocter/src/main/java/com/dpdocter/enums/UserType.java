package com.dpdocter.enums;

public enum UserType {

	ADMIN("ADMIN"),USER("USER"),DOCTOR("DOCTOR"),EXPERT("EXPERT");
	
	private String type;

	private UserType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
	
	
}
