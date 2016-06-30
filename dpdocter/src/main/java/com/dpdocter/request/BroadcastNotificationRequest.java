package com.dpdocter.request;

import com.dpdocter.beans.FileDetails;

public class BroadcastNotificationRequest {

	private String userType;
	
	private String message;

	private FileDetails image;
	
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

	public FileDetails getImage() {
		return image;
	}

	public void setImage(FileDetails image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "BroadcastNotificationRequest [userType=" + userType + ", message=" + message + ", image=" + image + "]";
	}
}
