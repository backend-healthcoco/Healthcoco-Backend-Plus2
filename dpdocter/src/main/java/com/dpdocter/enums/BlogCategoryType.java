package com.dpdocter.enums;

public enum BlogCategoryType {
	HEALTHCOCO("HEALTHCOCO");
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private BlogCategoryType(String type) {
		this.type = type;
	}

}
