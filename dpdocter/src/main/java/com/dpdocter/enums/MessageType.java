package com.dpdocter.enums;

public enum MessageType {

	ENGLISH("ENGLISH"), OTHERS("OTHERS");

	private String type;

	private MessageType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
