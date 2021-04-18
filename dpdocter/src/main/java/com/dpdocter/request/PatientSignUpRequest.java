package com.dpdocter.request;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.DOB;

public class PatientSignUpRequest {
    // user details
    private String firstName;

    private String lastName;

    private String middleName;

    private String userName;

    private char[] password;

    private String emailAddress;

    private String mobileNumber;

    // patient details
    private String bloodGroup;

    private DOB dob;

    private String gender;

    private Address address;

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

    public char[] getPassword() {
	return password;
    }

    public void setPassword(char[] password) {
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

    public String getBloodGroup() {
	return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
	this.bloodGroup = bloodGroup;
    }

    public DOB getDob() {
	return dob;
    }

    public void setDob(DOB dob) {
	this.dob = dob;
    }

    public String getGender() {
	return gender;
    }

    public void setGender(String gender) {
	this.gender = gender;
    }

    public Address getAddress() {
	return address;
    }

    public void setAddress(Address address) {
	this.address = address;
    }

    @Override
    public String toString() {
	return "PatientSignUpRequest [firstName=" + firstName + ", lastName=" + lastName + ", middleName=" + middleName + ", userName=" + userName
		+ ", password=" + password + ", emailAddress=" + emailAddress + ", mobileNumber=" + mobileNumber + ", bloodGroup=" + bloodGroup + ", dob=" + dob + ", gender=" + gender + ", address=" + address + "]";
    }

}
