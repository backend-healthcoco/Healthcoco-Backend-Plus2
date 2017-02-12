package com.dpdocter.enums;

public enum LocaleType {
	PHARMACY("PHARMACY"), SALOON("SALOON");

	private String type;

	public String getType() {
		return type;
	}

	private LocaleType(String type) {
		this.type = type;
	}

}
