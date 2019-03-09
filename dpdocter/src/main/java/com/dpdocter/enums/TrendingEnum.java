package com.dpdocter.enums;

public enum TrendingEnum {

	OFFER("OFFER"), BLOG("BLOG"), NEW_FEATURE("NEW_FEATURE");
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private TrendingEnum(String type) {
		this.type = type;
	}
}
