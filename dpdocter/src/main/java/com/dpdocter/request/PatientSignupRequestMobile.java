package com.dpdocter.request;

public class PatientSignupRequestMobile {
	private String name;

	private char[] password;

	private String mobileNumber;

	private String internalPromoCode;

	private boolean isNewPatientNeedToBeCreated;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public boolean isNewPatientNeedToBeCreated() {
		return isNewPatientNeedToBeCreated;
	}

	public void setNewPatientNeedToBeCreated(boolean isNewPatientNeedToBeCreated) {
		this.isNewPatientNeedToBeCreated = isNewPatientNeedToBeCreated;
	}

	public String getInternalPromoCode() {
		return internalPromoCode;
	}

	public void setInternalPromoCode(String internalPromoCode) {
		this.internalPromoCode = internalPromoCode;
	}

	@Override
	public String toString() {
		return "PatientSignupRequestMobile [name=" + name + ", password=" + password + ", mobileNumber=" + mobileNumber
				+ ", isNewPatientNeedToBeCreated=" + isNewPatientNeedToBeCreated + "]";
	}

}
