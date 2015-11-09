package com.dpdocter.request;

import com.dpdocter.beans.DOB;

public class DoctorSignupHandheldRequest {
    private String firstName;

    private String lastName;

    private String middleName;

    private String userName;

    private String password;

    private String emailAddress;

    private String mobileNumber;

    private String gender;

    private DOB dob;

    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public String getLastName() {
	return lastName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    public String getMiddleName() {
	return middleName;
    }

    public void setMiddleName(String middleName) {
	this.middleName = middleName;
    }

    public String getUserName() {
	return userName;
    }

    public void setUserName(String userName) {
	this.userName = userName;
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

    public String getGender() {
	return gender;
    }

    public void setGender(String gender) {
	this.gender = gender;
    }

    public DOB getDob() {
	return dob;
    }

    public void setDob(DOB dob) {
	this.dob = dob;
    }

    @Override
    public String toString() {
	return "DoctorSignupHandheldRequest [firstName=" + firstName + ", lastName=" + lastName + ", middleName=" + middleName + ", userName=" + userName
		+ ", password=" + password + ", emailAddress=" + emailAddress + ", mobileNumber=" + mobileNumber + ", gender=" + gender + ", dob=" + dob + "]";
    }
}
