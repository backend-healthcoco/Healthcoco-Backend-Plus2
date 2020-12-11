package com.dpdocter.beans;

public class LinkRequest {

	private String requestId;
	
	private String timestamp;
	
	private String transactionId;
	
	private LinkPatientRequest patient;

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

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public LinkPatientRequest getPatient() {
		return patient;
	}

	public void setPatient(LinkPatientRequest patient) {
		this.patient = patient;
	}
	
	
	
}
