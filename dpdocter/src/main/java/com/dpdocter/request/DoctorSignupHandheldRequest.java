package com.dpdocter.request;

import java.util.Arrays;
import java.util.List;

import com.dpdocter.beans.DOB;

public class DoctorSignupHandheldRequest {
	private String firstName;

	private String userName;

	private char[] password;

	private String emailAddress;

	private String mobileNumber;

	private String gender;

	private DOB dob;

	private List<String> specialities;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
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
		return emailAddress != null ? emailAddress.toLowerCase() : emailAddress;
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

	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}

	@Override
	public String toString() {
		return "DoctorSignupHandheldRequest [firstName=" + firstName + ", userName=" + userName + ", password="
				+ Arrays.toString(password) + ", emailAddress=" + emailAddress + ", mobileNumber=" + mobileNumber
				+ ", gender=" + gender + ", dob=" + dob + ", specialities=" + specialities + "]";
	}
}
