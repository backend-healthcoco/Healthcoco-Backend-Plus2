package com.dpdocter.enums;

public enum FieldAlign {
	VERTICAL("VERTICAL"), HORIZONTAL("HORIZONTAL");

	private String align;

	public String getAlign() {
		return align;
	}

	private FieldAlign(String align) {
		this.align = align;
	}

}
