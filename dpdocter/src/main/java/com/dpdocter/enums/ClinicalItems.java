package com.dpdocter.enums;

public enum ClinicalItems {

	COMPLAINTS("COMPLAINTS"),INVESTIGATIONS("INVESTIGATIONS"),OBSERVATIONS("OBSERVATIONS"),DIAGNOSIS("DIAGNOSIS"),
	NOTES("NOTES"),DIAGRAMS("DIAGRAMS");
	
	private String type;

	ClinicalItems(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
