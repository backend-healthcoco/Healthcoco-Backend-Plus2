package com.dpdocter.enums;

public enum TreatmentType {
	Aligners("Aligners");

	private String type;

	private TreatmentType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
}
