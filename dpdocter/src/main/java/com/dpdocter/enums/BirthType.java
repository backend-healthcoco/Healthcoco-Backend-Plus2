package com.dpdocter.enums;

public enum BirthType {

	VAGINAL("VAGINAL") , C_SECTION("C_SECTION");
	
	private String type;

	private BirthType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
	
	
}
