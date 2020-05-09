package com.dpdocter.enums;

public enum DoctorConsultation {

SMS("SMS"),VOICE("VOICE"),VIDEO("VIDEO");
	
	private String type;

	private DoctorConsultation(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
}
