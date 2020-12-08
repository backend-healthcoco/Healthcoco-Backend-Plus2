package com.dpdocter.beans;

import java.util.List;

public class NdhmAuthConfirm {

	private String accessToken;
	
	private NdhmPatient patient;
	
	private List<AuthConfirmIdentifier> identifier;

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

	public List<AuthConfirmIdentifier> getIdentifier() {
		return identifier;
	}

	public void setIdentifier(List<AuthConfirmIdentifier> identifier) {
		this.identifier = identifier;
	}
	
	
	
}
