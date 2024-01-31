package com.dpdocter.enums;

public enum BrandOfAligner {
	Toothsi("Toothsi");

	private String type;

	private BrandOfAligner(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
