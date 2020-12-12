package com.dpdocter.request;

public class GatewayConsentInitRequest {

	private String requestId;
	private String timestamp;
	private GatewayConsentInitRequestBody consent;
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
	public GatewayConsentInitRequestBody getConsent() {
		return consent;
	}
	public void setConsent(GatewayConsentInitRequestBody consent) {
		this.consent = consent;
	}
	
	

}
