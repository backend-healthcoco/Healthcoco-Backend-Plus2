package com.dpdocter.enums;

public enum ThyroidType {
	HYPERGLYCEMIA("HYPERGLYCEMIA"), HYPOGLYCEMIA("HYPOGLYCEMIA");

	private String type;

	public String getType() {
		return type;
	}

	private ThyroidType(String type) {
		this.type = type;
	}
}
