package com.dpdocter.enums;

public enum AppointmentType {

    APPOINTMENT("APPOINTMENT"), EVENT("EVENT"),ONLINE_CONSULTATION("ONLINE_CONSULTATION");

    private String type;

    public String getType() {
	return type;
    }

    private AppointmentType(String type) {
	this.type = type;
    }

}
