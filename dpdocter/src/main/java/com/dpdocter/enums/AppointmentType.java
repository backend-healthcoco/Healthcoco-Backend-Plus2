package com.dpdocter.enums;

public enum AppointmentType {

	NEW("NEW"), CONFIRM("CONFIRM"), RESCHEDULE("RESCHEDULE"), CANCEL("CANCEL");
	
	private String type;

	private AppointmentType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
