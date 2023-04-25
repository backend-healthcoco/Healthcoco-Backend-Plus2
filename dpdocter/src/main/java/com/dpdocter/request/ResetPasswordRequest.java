package com.dpdocter.request;

public class ResetPasswordRequest {
	private String userId;

	private char[] password;

	private String mobileNumber;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public char[] getPassword() {
		return password;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	@Override
	public String toString() {
		return "ResetPasswordRequest [userId=" + userId + ", password=" + password + ", mobileNumber=" + mobileNumber
				+ "]";
	}
}
