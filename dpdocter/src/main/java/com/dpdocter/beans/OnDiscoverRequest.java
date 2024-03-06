package com.dpdocter.beans;

public class OnDiscoverRequest {

	private String requestId;
	
	private String timestamp;
	
	private String transactionId;
	
	private DiscoverPatientResponse patient;
	
	private NdhmErrorObject error;
	
	private FetchResponse resp;

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

	public DiscoverPatientResponse getPatient() {
		return patient;
	}

	public void setPatient(DiscoverPatientResponse patient) {
		this.patient = patient;
	}

	public NdhmErrorObject getError() {
		return error;
	}

	public void setError(NdhmErrorObject error) {
		this.error = error;
	}

	public FetchResponse getResp() {
		return resp;
	}

	public void setResp(FetchResponse resp) {
		this.resp = resp;
	}
	
	

	
}
