package com.dpdocter.request;

public class LoginPatientRequest {

	private String mobileNumber;

	private String otpNumber;

	private char[] password;

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public char[] getPassword() {
		return password;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}

	public String getOtpNumber() {
		return otpNumber;
	}

	public void setOtpNumber(String otpNumber) {
		this.otpNumber = otpNumber;
	}

	@Override
	public String toString() {
		return "LoginPatientRequest [mobileNumber=" + mobileNumber + ", password=" + password + "]";
	}
}
