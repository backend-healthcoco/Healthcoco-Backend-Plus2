package com.dpdocter.enums;

public enum LineStyle {

	INLINE("inline"), BLOCK("block");

	private String style;

	private LineStyle(String style) {
		this.style = style;
	}

	public String getStyle() {
		return style;
	}

}
