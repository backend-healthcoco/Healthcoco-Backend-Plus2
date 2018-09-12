package com.dpdocter.enums;

public enum PhysicalStatusType {

	WORKING("WORKING"), PREGNANT("PREGNANT"), LACTING("LACTING"), NA("NA");

	private String type;

	public String getType() {
		return type;
	}

	PhysicalStatusType(String type) {
		this.type = type;
	}

}
