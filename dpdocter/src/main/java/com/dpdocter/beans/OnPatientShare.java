package com.dpdocter.beans;

import org.springframework.data.mongodb.core.mapping.Field;

public class OnPatientShare {

	private String requestId;
	
	private String timeStamp;
	
	private PatientQRCode patient;


	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public PatientQRCode getPatient() {
		return patient;
	}

	public void setPatient(PatientQRCode patient) {
		this.patient = patient;
	}

	
	
}
