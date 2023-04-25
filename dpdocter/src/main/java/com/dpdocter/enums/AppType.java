package com.dpdocter.enums;

public enum AppType {

	HEALTHCOCO("HEALTHCOCO"), HEALTHCOCO_PLUS("HEALTHCOCO_PLUS"), HEALTHCOCO_PAD("HEALTHCOCO_PAD");

	private String type;

	private AppType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
