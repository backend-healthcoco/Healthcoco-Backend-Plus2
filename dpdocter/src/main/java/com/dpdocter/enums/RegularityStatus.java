package com.dpdocter.enums;

public enum RegularityStatus {

	REGULAR("REGULAR"), NOT_SO_REGULAR("NOT_SO_REGULAR"), IRREGULAR("IRREGULAR"), NO_ACTION("NO_ACTION");

	private String type;

	public String getType() {
		return type;
	}

	private RegularityStatus(String type) {
		this.type = type;
	}

}
