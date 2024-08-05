package com.dpdocter.beans;

public class OnCareContext {
	private String abhaAddress;
	private String status;

	private String requestId;
	
	private String timestamp;
	
	private CareContextAcknowledgement acknowledgement;
	
	private NdhmErrorObject error;
	
	private FetchResponse response;

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

	public CareContextAcknowledgement getAcknowledgement() {
		return acknowledgement;
	}

	public void setAcknowledgement(CareContextAcknowledgement acknowledgement) {
		this.acknowledgement = acknowledgement;
	}

	public NdhmErrorObject getError() {
		return error;
	}

	public void setError(NdhmErrorObject error) {
		this.error = error;
	}


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public FetchResponse getResponse() {
		return response;
	}

	public void setResponse(FetchResponse response) {
		this.response = response;
	}

	public String getAbhaAddress() {
		return abhaAddress;
	}

	public void setAbhaAddress(String abhaAddress) {
		this.abhaAddress = abhaAddress;
	}
	
}
