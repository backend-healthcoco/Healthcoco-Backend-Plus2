package com.dpdocter.enums;

public enum ContactsSearchType {

	DOCTORCONTACTS("DOCTORCONTACTS"), RECENTLYVISITED("RECENTLYVISITED"), MOSTVISITED("MOSTVISITED"),
	RECENTLYADDED("RECENTLYADDED");

	private String type;

	private ContactsSearchType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
