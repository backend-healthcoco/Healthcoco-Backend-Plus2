package com.dpdocter.beans;

public class NotifyHiuRequest {

	private String requestId;
	
	private String timestamp;
	
	private HiuNotify notification;

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

	public HiuNotify getNotification() {
		return notification;
	}

	public void setNotification(HiuNotify notification) {
		this.notification = notification;
	}
	
	
}
