package com.dpdocter.enums;

public enum MessageStatusType {
	Delivered("Delivered"), Sent("Sent"), Undelivered("Undelivered"), Failed("Failed");

	private String type;

	private MessageStatusType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
