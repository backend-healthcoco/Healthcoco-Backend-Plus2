package com.dpdocter.enums;

public enum RegistrationType {

	REGISTRATION_DETAILS("REGISTRATION_DETAILS"),CLINIC_OWNERSHIP("CLINIC_OWNERSHIP"),PHOTOID_PROOF("PHOTOID_PROOF");
	
	private String type;

	public String getType() {
		return type;
	}


	private RegistrationType(String type) {
		this.type = type;
	}
	
	
}
