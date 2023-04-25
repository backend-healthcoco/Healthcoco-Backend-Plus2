package com.dpdocter.enums;

public enum CitySearchType {

	LANDMARK("LANDMARK"), LOCALITY("LOCALITY");

	private String type;

	private CitySearchType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
