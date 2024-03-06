package com.dpdocter.beans;

public class AuthConfirmRequest {

	private String requestId;
	
	private String timestamp;
	
	private String transactionId;
	
	private AuthCredRequest credential;

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

	public AuthCredRequest getCredential() {
		return credential;
	}

	public void setCredential(AuthCredRequest credential) {
		this.credential = credential;
	}
	
	
	
	
}
