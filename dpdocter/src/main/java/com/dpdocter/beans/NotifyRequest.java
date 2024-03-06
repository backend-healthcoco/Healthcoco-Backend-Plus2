package com.dpdocter.beans;

public class NotifyRequest {

	private String requestId;
	
	private String timestamp;
	
	private NdhmNotification notification;

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

	public NdhmNotification getNotification() {
		return notification;
	}

	public void setNotification(NdhmNotification notification) {
		this.notification = notification;
	}
	
	
}
