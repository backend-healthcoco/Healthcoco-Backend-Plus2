package com.dpdocter.request;

public class ForgotUsernamePasswordRequest {
	private String username;
	private String phoneNumber;
	private String emailAddress;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	@Override
	public String toString() {
		return "ResetPasswordRequest [username=" + username + ", phoneNumber=" + phoneNumber + ", emailAddress=" + emailAddress + "]";
	}

}
