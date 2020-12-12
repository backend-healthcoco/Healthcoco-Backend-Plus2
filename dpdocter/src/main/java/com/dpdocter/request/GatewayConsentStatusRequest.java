package com.dpdocter.request;

public class GatewayConsentStatusRequest {

	private String requestId;
	private String timestamp;
	private String consentRequestId;
	
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
	public String getConsentRequestId() {
		return consentRequestId;
	}
	public void setConsentRequestId(String consentRequestId) {
		this.consentRequestId = consentRequestId;
	}
	
	
}
