package com.dpdocter.enums;

public enum BlogSuperCategoryType {

	HEALTH("HEALTH"), WELLNESS("WELLNESS"), HAPPINESS("HAPPINESS"), POPULAR("POPULAR"), TRENDING("TRENDING");

	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private BlogSuperCategoryType(String type) {
		this.type = type;
	}
}
