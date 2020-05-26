package com.dpdocter.enums;

public enum Nature {

	CONTINUOUS("CONTIINUOUS"),INTERMITTENT("INTERMITTENT");
	
	String type;

	public String getType() {
		return type;
	}

	
	private Nature(String type) {
		this.type = type;
	}
	
}
