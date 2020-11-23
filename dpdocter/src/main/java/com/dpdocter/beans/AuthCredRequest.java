package com.dpdocter.beans;

public class AuthCredRequest {

	private String authCode;
	
	private AuthDemographic demographic;
	
	

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public AuthDemographic getDemographic() {
		return demographic;
	}

	public void setDemographic(AuthDemographic demographic) {
		this.demographic = demographic;
	}

		
	
}
