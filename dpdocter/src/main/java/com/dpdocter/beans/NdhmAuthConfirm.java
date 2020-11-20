package com.dpdocter.beans;

public class NdhmAuthConfirm {

	private String accessToken;
	
	private NdhmPatient patient;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public NdhmPatient getPatient() {
		return patient;
	}

	public void setPatient(NdhmPatient patient) {
		this.patient = patient;
	}
	
	
}
