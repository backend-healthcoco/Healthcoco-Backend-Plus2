package com.dpdocter.request;

public class SearchRequest {

	private String firstName;

	private String lastName;

	private String middleName;

	private String userName;

	private String phoneNumber;

	private String secPhoneNumber;

	private String emailAddress;

	private String PID;

	private String doctorId;

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

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getSecPhoneNumber() {
		return secPhoneNumber;
	}

	public void setSecPhoneNumber(String secPhoneNumber) {
		this.secPhoneNumber = secPhoneNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPID() {
		return PID;
	}

	public void setPID(String pID) {
		PID = pID;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	@Override
	public String toString() {
		return "SearchRequest [firstName=" + firstName + ", lastName=" + lastName + ", middleName=" + middleName
				+ ", userName=" + userName + ", phoneNumber=" + phoneNumber + ", secPhoneNumber=" + secPhoneNumber
				+ ", emailAddress=" + emailAddress + ", PID=" + PID + ", doctorId=" + doctorId + "]";
	}

}
