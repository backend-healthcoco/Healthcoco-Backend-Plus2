package com.dpdocter.enums;

public enum AppointmentWaitTime {

	LESS_THAN_FIFTEEN_MIN("LESS_THAN_FIFTEEN_MIN"), FIFTEEN_MIN_TO_THIRTY_MIN("FIFTEEN_MIN_TO_THIRTY_MIN"),
	THIRTY_MIN_TO_ONE_HOUR("THIRTY_MIN_TO_ONE_HOUR"), MORE_THAN_ONE_HOUR("MORE_THAN_ONE_HOUR");

	private String type;

	public String getType() {
		return type;
	}

	private AppointmentWaitTime(String type) {
		this.type = type;
	}

}
