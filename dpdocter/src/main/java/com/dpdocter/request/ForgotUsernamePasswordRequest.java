package com.dpdocter.request;

public class ForgotUsernamePasswordRequest {
	private String username;

	private String mobileNumber;

	private String emailAddress;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	@Override
	public String toString() {
		return "ForgotUsernamePasswordRequest [username=" + username + ", mobileNumber=" + mobileNumber
				+ ", emailAddress=" + emailAddress + "]";
	}

}
