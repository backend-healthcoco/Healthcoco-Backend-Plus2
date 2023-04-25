package com.dpdocter.enums;

public enum SMSFormatType {

	CONFIRMED_APPOINTMENT("CONFIRMED_APPOINTMENT"), CANCEL_APPOINTMENT("CANCEL_APPOINTMENT"),
	APPOINTMENT_REMINDER("APPOINTMENT_REMINDER"), APPOINTMENT_FOLLOWUP("APPOINTMENT_FOLLOWUP"),
	APPOINTMENT_SCHEDULE("APPOINTMENT_SCHEDULE");

	private String type;

	public String getType() {
		return type;
	}

	private SMSFormatType(String type) {
		this.type = type;
	}
}
