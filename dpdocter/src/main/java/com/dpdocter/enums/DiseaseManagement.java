package com.dpdocter.enums;

public enum DiseaseManagement {

	/*
	 * 1) Diabetes Control2) Cholesterol Control3) Thyroid Control4) Hypertension
	 * Control5) PCOD Control
	 * 
	 * 6) Other
	 */
	DIABETES("DIABETES") , CHOLESTEROL("CHOLESTEROL"),THYROID("THYROID"),HYPERTENSION("HYPERTENSION"),PCOD("PCOD"),OTHER("OTHER"),NA("NA");
	
	private String type;

	public String getType() {
		return type;
	}

	private DiseaseManagement(String type) {
		this.type = type;
	}

	
	
	
	
}
