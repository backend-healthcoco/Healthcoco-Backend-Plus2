package com.dpdocter.beans;

import java.util.List;

public class PatientDownloadData {

	private Long registrationDate;
	
	private String date;
	
	private String patientId;
	
	private String localPatientName;
	
	private String mobileNumber;
	
	private String emailAddress;
	
	private String secMobile;
	
	private String gender;
	
	private String country;

	private String city;

	private String state;

	private String postalCode;

	private String locality;

	private String landmarkDetails;

	private String streetAddress;
	
	private String adhaarId;

	private String panCardNumber;

	private String drivingLicenseId;

	private String insuranceId;
	
	private String dateOfBirth;
	
	private String age;
	
	private String bloodGroup;
	
	private String referredBy;
	
	private List<String> groups;

	private DOB dob;
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getPatientId() {
		return patientId;
	}
	
	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getSecMobile() {
		return secMobile;
	}

	public void setSecMobile(String secMobile) {
		this.secMobile = secMobile;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
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

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getLandmarkDetails() {
		return landmarkDetails;
	}

	public void setLandmarkDetails(String landmarkDetails) {
		this.landmarkDetails = landmarkDetails;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getAdhaarId() {
		return adhaarId;
	}

	public void setAdhaarId(String adhaarId) {
		this.adhaarId = adhaarId;
	}

	public String getPanCardNumber() {
		return panCardNumber;
	}

	public void setPanCardNumber(String panCardNumber) {
		this.panCardNumber = panCardNumber;
	}

	public String getDrivingLicenseId() {
		return drivingLicenseId;
	}

	public void setDrivingLicenseId(String drivingLicenseId) {
		this.drivingLicenseId = drivingLicenseId;
	}

	public String getInsuranceId() {
		return insuranceId;
	}

	public void setInsuranceId(String insuranceId) {
		this.insuranceId = insuranceId;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public String getReferredBy() {
		return referredBy;
	}

	public void setReferredBy(String referredBy) {
		this.referredBy = referredBy;
	}

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
	}

	public Long getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Long registrationDate) {
		this.registrationDate = registrationDate;
	}

	@Override
	public String toString() {
		return "PatientDownloadData [registrationDate=" + registrationDate + ", date=" + date + ", patientId="
				+ patientId + ", localPatientName=" + localPatientName + ", mobileNumber=" + mobileNumber
				+ ", emailAddress=" + emailAddress + ", secMobile=" + secMobile + ", gender=" + gender + ", country="
				+ country + ", city=" + city + ", state=" + state + ", postalCode=" + postalCode + ", locality="
				+ locality + ", landmarkDetails=" + landmarkDetails + ", streetAddress=" + streetAddress + ", adhaarId="
				+ adhaarId + ", panCardNumber=" + panCardNumber + ", drivingLicenseId=" + drivingLicenseId
				+ ", insuranceId=" + insuranceId + ", dateOfBirth=" + dateOfBirth + ", age=" + age + ", bloodGroup="
				+ bloodGroup + ", referredBy=" + referredBy + ", groups=" + groups + ", dob=" + dob + "]";
	}

}
