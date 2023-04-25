package com.dpdocter.response;

import com.dpdocter.enums.RoleEnum;

public class ForgotPasswordResponse {
	private String username;

	private String mobileNumber;

	private String emailAddress;

	private RoleEnum role;

	public ForgotPasswordResponse(String username, String mobileNumber, String emailAddress, RoleEnum role) {
		this.username = username;
		this.mobileNumber = mobileNumber;
		this.emailAddress = emailAddress;
		this.role = role;
	}

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

	public RoleEnum getRole() {
		return role;
	}

	public void setRole(RoleEnum role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "ForgotPasswordResponse [username=" + username + ", mobileNumber=" + mobileNumber + ", emailAddress="
				+ emailAddress + ", role=" + role + "]";
	}

}
