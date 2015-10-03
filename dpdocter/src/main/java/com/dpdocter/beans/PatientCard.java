package com.dpdocter.beans;

import java.util.List;

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

    private String gender;

    private String mobileNumber;

    private String secPhoneNumber;

    private DOB dob;

    private int count;

    private Long dateOfVisit;

    private List<Group> groups;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private String doctorSepecificPatientId;

    private String colorCode;

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

    public String getGender() {
	return gender;
    }

    public void setGender(String gender) {
	this.gender = gender;
    }

    public String getMobileNumber() {
	return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
	this.mobileNumber = mobileNumber;
    }

    public String getSecPhoneNumber() {
	return secPhoneNumber;
    }

    public void setSecPhoneNumber(String secPhoneNumber) {
	this.secPhoneNumber = secPhoneNumber;
    }

    public DOB getDob() {
	return dob;
    }

    public void setDob(DOB dob) {
	this.dob = dob;
    }

    public int getCount() {
	return count;
    }

    public void setCount(int count) {
	this.count = count;
    }

    public Long getDateOfVisit() {
	return dateOfVisit;
    }

    public void setDateOfVisit(Long dateOfVisit) {
	this.dateOfVisit = dateOfVisit;
    }

    public List<Group> getGroups() {
	return groups;
    }

    public void setGroups(List<Group> groups) {
	this.groups = groups;
    }

    public String getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(String doctorId) {
	this.doctorId = doctorId;
    }

    public String getLocationId() {
	return locationId;
    }

    public void setLocationId(String locationId) {
	this.locationId = locationId;
    }

    public String getHospitalId() {
	return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
	this.hospitalId = hospitalId;
    }

    public String getDoctorSepecificPatientId() {
	return doctorSepecificPatientId;
    }

    public void setDoctorSepecificPatientId(String doctorSepecificPatientId) {
	this.doctorSepecificPatientId = doctorSepecificPatientId;
    }

    public String getColorCode() {
	return colorCode;
    }

    public void setColorCode(String colorCode) {
	this.colorCode = colorCode;
    }

    @Override
    public String toString() {
	return "PatientCard [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", middleName=" + middleName + ", userName=" + userName
		+ ", emailAddress=" + emailAddress + ", imageUrl=" + imageUrl + ", bloodGroup=" + bloodGroup + ", PID=" + PID + ", gender=" + gender
		+ ", mobileNumber=" + mobileNumber + ", secPhoneNumber=" + secPhoneNumber + ", dob=" + dob + ", count=" + count + ", dateOfVisit="
		+ dateOfVisit + ", groups=" + groups + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
		+ ", doctorSepecificPatientId=" + doctorSepecificPatientId + ", colorCode=" + colorCode + "]";
    }
}
