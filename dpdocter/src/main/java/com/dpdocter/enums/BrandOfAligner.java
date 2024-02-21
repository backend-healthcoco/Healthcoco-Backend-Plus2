package com.dpdocter.enums;

public enum BrandOfAligner {
	Invisalign("Invisalign"), Flash("Flash"), _32_Watts("32 Watts"), Smilebird("Smilebird"), Illusion("Illusion"),
	Internal("Internal"), Other("Other"), Toothsi("Toothsi");

	private String type;

	private BrandOfAligner(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
