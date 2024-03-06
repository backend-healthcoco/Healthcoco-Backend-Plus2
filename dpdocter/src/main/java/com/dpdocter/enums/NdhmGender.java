package com.dpdocter.enums;

public enum NdhmGender {

	M("M"), F("F"), O("O"), U("U") ;
	
	private String type;

	private NdhmGender(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
	
	
}
