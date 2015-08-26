package com.dpdocter.enums;

public enum Range {
	GLOBAL("GLOBAL"),CUSTOM("CUSTOM"),BOTH("BOTH");
	
	private String range;

	private Range(String range) {
		this.range = range;
	}

	public String getRange() {
		return range;
	}
}
