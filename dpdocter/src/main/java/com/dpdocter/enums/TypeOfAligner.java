package com.dpdocter.enums;

public enum TypeOfAligner {
	Upper("Upper"),Lower("Lower");

	private String type;

	private TypeOfAligner(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
}
