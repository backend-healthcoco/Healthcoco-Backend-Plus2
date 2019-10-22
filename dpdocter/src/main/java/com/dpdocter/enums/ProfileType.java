package com.dpdocter.enums;

public enum ProfileType {
 STUDENT("STUDENT"),TEACHER("TEACHER");

	private String type;

	public String getType() {
		return type;
	}

	private ProfileType(String type) {
		this.type = type;
	}
	
}
