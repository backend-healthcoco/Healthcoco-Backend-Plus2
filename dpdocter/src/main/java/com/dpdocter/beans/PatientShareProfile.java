package com.dpdocter.beans;

import org.springframework.data.mongodb.core.mapping.Field;

public class PatientShareProfile {

	private String requestId;
	
	private String timestamp;

	private PatientQRCode patient;

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

	public PatientQRCode getPatient() {
		return patient;
	}

	public void setPatient(PatientQRCode patient) {
		this.patient = patient;
	}

	

	
	
	
}
