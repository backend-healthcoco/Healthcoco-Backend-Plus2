package com.dpdocter.enums;

public enum SearchType {

	DAILY("DAILY"), WEEKLY("WEEKLY"), MONTHLY("MONTHLY"), YEARLY("YEARLY");

	private String type;

	public String getType() {
		return type;
	}

	private SearchType(String type) {
		this.type = type;
	}

}
