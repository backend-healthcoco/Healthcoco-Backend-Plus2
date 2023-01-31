package com.dpdocter.enums;

public enum AppointmentType {

    APPOINTMENT("APPOINTMENT"), EVENT("EVENT"),ONLINE_CONSULTATION("ONLINE_CONSULTATION"),
    DENTAL_CHAIN_APPOINTMENT("DENTAL_CHAIN_APPOINTMENT");
    private String type;

    public String getType() {
	return type;
    }

    private AppointmentType(String type) {
	this.type = type;
    }

}
