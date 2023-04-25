package com.dpdocter.enums;

public enum TestSampleType {

	BLOOD("BLOOD"), URINE("URINE"), HAIR("HAIR"), SERUM("SERUM"), PLASMA("PLASMA");

	private String type;

	public String getType() {
		return type;
	}

	private TestSampleType(String type) {
		this.type = type;
	}

}
