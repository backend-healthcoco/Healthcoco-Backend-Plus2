package com.dpdocter.enums;

public enum FONTSTYLE {

	BOLD("BOLD"), ITALIC("ITALIC");

	private String style;

	public String getStyle() {
		return style;
	}

	private FONTSTYLE(String style) {
		this.style = style;
	}
}
