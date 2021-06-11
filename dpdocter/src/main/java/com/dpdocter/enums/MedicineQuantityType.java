package com.dpdocter.enums;

public enum MedicineQuantityType {

STRIPS("STRIPS"),TABLETS("TABLETS"),BOTTLE("BOTTLE"),SYRUP("SYRUP"),TUBE("TUBE"),INJECTION("INJECTION"),GEL("GEL"),CREAM("CREAM");
	
	private String type;

	private MedicineQuantityType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
