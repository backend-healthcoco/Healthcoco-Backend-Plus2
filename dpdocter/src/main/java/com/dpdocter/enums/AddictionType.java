package com.dpdocter.enums;

public enum AddictionType {
	TOBACCO("TOBACOO"), SMOCKING("SMOCKING"), ALCOHOL("ALCOHOL"), OTHER("OTHER");

	private String type;

	public String getType() {
		return type;
	}

	private AddictionType(String type) {
		this.type = type;
	}

}
