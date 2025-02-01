package com.dpdocter.enums;

public enum PrintSettingType {
	
	DEFAULT("DEFAULT"),BILLING("BILLING"),RECEIPT("RECEIPT"),EMR("EMR"),IPD("IPD"),EMAIL("EMAIL");
	

	private String type;

	private PrintSettingType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
