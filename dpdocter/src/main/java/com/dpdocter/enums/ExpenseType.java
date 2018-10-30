package com.dpdocter.enums;

public enum ExpenseType {
	LAB_CHARGES("LAB_CHARGES"), TELEPHONE_BILL("TELEPHONE_BILL"), WATER_BILL("WATER_BILL"),
	ELECTRICITY_BILL("ELECTRICITY_BILL"), SALARY("SALARY"), MATERIALS("MATERIALS"), PERSONALS("PERSONALS"),
	OTHER("OTHERS"),ENRICH_DISTRIBUTORS("ENRICH_DISTRIBUTORS");
	private String type;

	public String getType() {
		return type;
	}

	private ExpenseType(String type) {
		this.type = type;
	}

}
