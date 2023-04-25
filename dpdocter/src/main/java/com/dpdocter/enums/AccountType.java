package com.dpdocter.enums;

public enum AccountType {
	SAVING("SAVING"), CURRENT("CURRENT");

	private String type;

	private AccountType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
