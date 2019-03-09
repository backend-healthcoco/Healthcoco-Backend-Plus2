package com.dpdocter.enums;

public enum OfferCategaryType {
	HEALTH("HEALTH"), WELLNESS("WELLNESS"), HAPPINESS("HAPPINESS");

	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private OfferCategaryType(String type) {
		this.type = type;
	}

}
