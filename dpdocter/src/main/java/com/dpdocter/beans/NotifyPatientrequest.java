package com.dpdocter.beans;

public class NotifyPatientrequest {

	private String requestId;
	
	private String timestamp;
	
	private SmsPatientNotify notification;
	
	

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

	public SmsPatientNotify getNotification() {
		return notification;
	}

	public void setNotification(SmsPatientNotify notification) {
		this.notification = notification;
	}

	
	
	
}
