package com.dpdocter.beans;

public class CareContextDiscoverRequest {

	private String requestId;
	
	private String timestamp;
	
	private String transactionId;
	
	private DiscoverPatient patient;

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

	public DiscoverPatient getPatient() {
		return patient;
	}

	public void setPatient(DiscoverPatient patient) {
		this.patient = patient;
	}
	
	
	
}
