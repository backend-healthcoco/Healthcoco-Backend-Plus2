package com.dpdocter.beans;

import java.util.List;

public class RegisteredPatientDetails {

	private String firstName;

	private String lastName;

	private String middleName;

	private String imageUrl;

	private DOB dob;

	private String userId;

	private String userName;

	private String mobileNumber;

	private String gender;

	private Patient patient;

	private Address address;

	private List<String> groups;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

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

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
	}

	@Override
	public String toString() {
		return "RegisteredPatientDetails [firstName=" + firstName + ", lastName=" + lastName + ", middleName=" + middleName + ", imageUrl=" + imageUrl
				+ ", dob=" + dob + ", userId=" + userId + ", userName=" + userName + ", mobileNumber=" + mobileNumber + ", gender=" + gender + ", patient="
				+ patient + ", address=" + address + ", groups=" + groups + "]";
	}

}
