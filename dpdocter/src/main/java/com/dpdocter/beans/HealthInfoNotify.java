package com.dpdocter.beans;

public class HealthInfoNotify {

	private String requestId;
	
	private String timestamp;
	
	private HipInfoNotify notification;

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

	public HipInfoNotify getNotification() {
		return notification;
	}

	public void setNotification(HipInfoNotify notification) {
		this.notification = notification;
	}
	
	
	
	
}
