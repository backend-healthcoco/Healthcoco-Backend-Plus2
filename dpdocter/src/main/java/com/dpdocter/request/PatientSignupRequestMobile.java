package com.dpdocter.request;

public class PatientSignupRequestMobile {
    private String name;

    private String password;

    private String emailAddress;

    private String mobileNumber;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public String getEmailAddress() {
	return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
	this.emailAddress = emailAddress;
    }

    public String getMobileNumber() {
	return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
	this.mobileNumber = mobileNumber;
    }

    @Override
    public String toString() {
	return "PatientSignupRequestMobile [name=" + name + ", password=" + password + ", emailAddress=" + emailAddress + ", mobileNumber=" + mobileNumber
		+ "]";
    }

}
