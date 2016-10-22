package com.dpdocter.enums;

public enum QuantityEnum {
	DAYS("DAYS"),QTY("QTY");
	private String duration;

	public String getDuration() {
		return duration;
	}

	private QuantityEnum(String duration) {
		this.duration = duration;
	}
	

}
