package com.dpdocter.request;

import com.dpdocter.beans.DOB;
import com.dpdocter.beans.PersonalInformation;

public class PatientSignupRequestMobile {

	private String countryCode = "+91";

	private String name;

	private char[] password;

	private String mobileNumber;

	private String firstName;

	private String gender;

	private DOB dob;

	private String internalPromoCode;

	private boolean isNewPatientNeedToBeCreated;

	private String emailAddress;

	private PersonalInformation personalInformation;

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

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
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

	public PersonalInformation getPersonalInformation() {
		return personalInformation;
	}

	public void setPersonalInformation(PersonalInformation personalInformation) {
		this.personalInformation = personalInformation;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	@Override
	public String toString() {
		return "PatientSignupRequestMobile [name=" + name + ", password=" + password + ", mobileNumber=" + mobileNumber
				+ ", isNewPatientNeedToBeCreated=" + isNewPatientNeedToBeCreated + "]";
	}

}
