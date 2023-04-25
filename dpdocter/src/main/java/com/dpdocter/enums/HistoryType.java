package com.dpdocter.enums;

public enum HistoryType {

	PAST("PAST"), FAMILY("FAMILY"), PRESENT("PRESENT"), PERSONAL("PERSONAL"), DRUG_ALLERGIES("DRUG_ALLERGIES"),
	BIRTH("BIRTH"), MEDICAL("MEDICAL");

	private String type;

	private HistoryType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
