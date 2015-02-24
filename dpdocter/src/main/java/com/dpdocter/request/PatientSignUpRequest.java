package com.dpdocter.request;

import com.dpdocter.beans.DOB;



public class PatientSignUpRequest {
	// user details
	private String firstName;
	private String lastName;
	private String middleName;
	private String userName;
	private String password;
	private String emailAddress;
	private String phoneNumber;
	//patient details
	private String bloodGroup;
	private String imageUrl;
	private DOB dob;
	private String gender;
	//address
	private String country;
	private String city;
	private String state;
	private String postalCode;
	private String roadNumber;
	private String roadName;
	private String houseNumber;
	private Double latitude;
	private Double longitude;
	
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
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getBloodGroup() {
		return bloodGroup;
	}
	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getRoadNumber() {
		return roadNumber;
	}
	public void setRoadNumber(String roadNumber) {
		this.roadNumber = roadNumber;
	}
	public String getRoadName() {
		return roadName;
	}
	public void setRoadName(String roadName) {
		this.roadName = roadName;
	}
	public String getHouseNumber() {
		return houseNumber;
	}
	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
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
	@Override
	public String toString() {
		return "PatientSignUpRequest [firstName=" + firstName + ", lastName="
				+ lastName + ", middleName=" + middleName + ", userName="
				+ userName + ", password=" + password + ", emailAddress="
				+ emailAddress + ", phoneNumber=" + phoneNumber
				+ ", bloodGroup=" + bloodGroup + ", imageUrl=" + imageUrl
				+ ", dob=" + dob + ", gender=" + gender + ", country="
				+ country + ", city=" + city + ", state=" + state
				+ ", postalCode=" + postalCode + ", roadNumber=" + roadNumber
				+ ", roadName=" + roadName + ", houseNumber=" + houseNumber
				+ ", latitude=" + latitude + ", longitude=" + longitude + "]";
	}
	
	
	

	
	
}
