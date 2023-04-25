package com.dpdocter.enums;

public enum DeviceType {

	ANDROID("ANDROID"), IOS("IOS"), WINDOWS("WINDOWS"), IPAD("IPAD"), WEB("WEB"), ANDROID_PAD("ANDROID_PAD"),
	WEB_ADMIN("WEB_ADMIN");

	private String type;

	private DeviceType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
