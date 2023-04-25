package com.dpdocter.enums;

public enum PushNotificationType {

	BROADCAST("BROADCAST"), INDIVIDUAL("INDIVIDUAL");

	private String type;

	public String getType() {
		return type;
	}

	private PushNotificationType(String type) {
		this.type = type;
	}
}
