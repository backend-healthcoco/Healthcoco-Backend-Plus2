package com.dpdocter.enums;

public enum RetainerType {
	NOT_REQUIRED("Not Required"), PERMANANT_RETAINER("Permanent Retainer"), TEMPORARY_RETAINER("Temporary Retainer"),
	HAWLEY_RETAINER("Hawley Retainer"), CLEAR_PLASTIC_RETAINER("Clear Plastic Retainer");

	private String type;

	private RetainerType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
