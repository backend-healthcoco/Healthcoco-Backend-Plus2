package com.dpdocter.enums;

public enum NotificationType {

	PRESCRIPTION("PRESCRIPTION");
	
	private String type;

	public String getType() {
		return type;
	}

	private NotificationType(String type) {
		this.type = type;
	}
	
}
