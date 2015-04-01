package com.dpdocter.response;

import com.dpdocter.enums.RoleEnum;

public class ForgotPasswordResponse {
	private String username;
	private String phoneNumber;
	private String emailAddress;
	private RoleEnum role;

	public ForgotPasswordResponse(String username, String phoneNumber, String emailAddress, RoleEnum role) {
		this.username = username;
		this.phoneNumber = phoneNumber;
		this.emailAddress = emailAddress;
		this.role = role;
	}

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

	public RoleEnum getRole() {
		return role;
	}

	public void setRole(RoleEnum role) {
		this.role = role;
	}

}
