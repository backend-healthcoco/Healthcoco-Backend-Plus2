package com.dpdocter.webservices;

public class GateWayHiOnRequest {

	private String transactionId;
	private String sessionStatus= "ACKNOWLEDGED";
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getSessionStatus() {
		return sessionStatus;
	}
	public void setSessionStatus(String sessionStatus) {
		this.sessionStatus = sessionStatus;
	}
	
	
}
