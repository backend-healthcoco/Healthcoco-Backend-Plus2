package com.dpdocter.beans;

public class CareContextLink {

	private String accessToken;
	
	private CareContextPatient patient;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public CareContextPatient getPatient() {
		return patient;
	}

	public void setPatient(CareContextPatient patient) {
		this.patient = patient;
	}
	
	
	
	
	
}
