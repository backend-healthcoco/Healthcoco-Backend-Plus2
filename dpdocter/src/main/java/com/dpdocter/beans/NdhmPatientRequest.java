package com.dpdocter.beans;

public class NdhmPatientRequest {

	private String requestId;
	
	private String timestamp;
	
	private PatientQuery query;

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

	public PatientQuery getQuery() {
		return query;
	}

	public void setQuery(PatientQuery query) {
		this.query = query;
	}
	
	
	
	
}
