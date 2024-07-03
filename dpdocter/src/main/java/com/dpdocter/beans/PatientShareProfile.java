package com.dpdocter.beans;

public class PatientShareProfile {

	private String requestId;
	
	private String timestamp;

	private PatientQRCode profile;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public PatientQRCode getProfile() {
		return profile;
	}

	public void setProfile(PatientQRCode profile) {
		this.profile = profile;
	}

	

	

	
	
	
}
