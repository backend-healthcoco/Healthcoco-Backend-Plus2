package com.dpdocter.beans;

public class PatientCard {
	private String id;
	private String firstName;
	private String lastName;
	private String middleName;
	private String userName;
	private String emailAddress;
	private String imageUrl;
	private String bloodGroup;
	private String PID;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getBloodGroup() {
		return bloodGroup;
	}
	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}
	public String getPID() {
		return PID;
	}
	public void setPID(String pID) {
		PID = pID;
	}
	@Override
	public String toString() {
		return "PatientCard [id=" + id + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", middleName=" + middleName
				+ ", userName=" + userName + ", emailAddress=" + emailAddress
				+ ", imageUrl=" + imageUrl + ", bloodGroup=" + bloodGroup
				+ ", PID=" + PID + "]";
	}
	
	
	
}
