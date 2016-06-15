package com.dpdocter.enums;

public enum DeviceType {

	ANDROID("ANDROID"),IOS("IOS"),WINDOWS("WINDOWS");
	
	private String type;

	private DeviceType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
