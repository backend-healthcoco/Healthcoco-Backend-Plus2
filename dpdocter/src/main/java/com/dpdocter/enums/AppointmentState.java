package com.dpdocter.enums;

public enum AppointmentState {

	CONFIRM("CONFIRM"), NEW("NEW"), CANCEL("CANCEL"), RESCHEDULE("RESCHEDULE"), PENDING("PENDING");

	private String state;

	public String getState() {
		return state;
	}

	private AppointmentState(String state) {
		this.state = state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
