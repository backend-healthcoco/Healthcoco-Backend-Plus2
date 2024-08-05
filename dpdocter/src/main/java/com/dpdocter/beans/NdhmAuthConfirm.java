package com.dpdocter.beans;

public class NdhmAuthConfirm {

	private String accessToken;
	
	private NdhmPatient patient;
	private NdhmValidity validity;

	
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

	public NdhmValidity getValidity() {
		return validity;
	}

	public void setValidity(NdhmValidity validity) {
		this.validity = validity;
	}

	
	
}
