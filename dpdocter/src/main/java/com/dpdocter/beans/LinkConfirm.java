package com.dpdocter.beans;

public class LinkConfirm {

	private String requestId;
	
	private String timestamp;
	
	private ConfirmationObject confirmation;

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

	public ConfirmationObject getConfirmation() {
		return confirmation;
	}

	public void setConfirmation(ConfirmationObject confirmation) {
		this.confirmation = confirmation;
	}
	
	
}
