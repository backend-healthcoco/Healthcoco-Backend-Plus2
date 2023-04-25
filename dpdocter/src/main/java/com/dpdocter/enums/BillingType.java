package com.dpdocter.enums;

public enum BillingType {

	SETTLE("SETTLE"), NONSETTLE("NONSETTLE"), BOTH("BOTH");

	private String type;

	public String getType() {
		return type;
	}

	private BillingType(String type) {
		this.type = type;
	}
}
