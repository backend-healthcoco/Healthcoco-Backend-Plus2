package com.dpdocter.enums;

public enum LabType {

	DIAGNOSTIC("DIAGNOSTIC"), DENTAL("DENTAL");

	private String type;

	public String getType() {
		return type;
	}

	private LabType(String type) {
		this.type = type;
	}
}
