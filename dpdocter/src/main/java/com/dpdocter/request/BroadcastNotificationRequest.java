package com.dpdocter.request;

public class BroadcastNotificationRequest {

	private String userType;
	
	private String message;

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "BroadcastNotificationRequest [userType=" + userType + ", message=" + message + "]";
	}
}
