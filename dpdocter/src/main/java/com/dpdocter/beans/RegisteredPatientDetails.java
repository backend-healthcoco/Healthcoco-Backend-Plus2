package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class RegisteredPatientDetails extends GenericCollection {

    private String firstName;

    private String lastName;

    private String middleName;

    private String imageUrl;

    private String thumbnailUrl;

    private DOB dob;

    private String userId;

    private String userName;
	private String countryCode;

    private String mobileNumber;

    private String gender;

    private Patient patient;

    private Address address;

    private List<Group> groups;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private String PID;

    private String colorCode;

    private Reference referredBy;

    private Boolean isPartOfClinic;
    
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

    public List<Group> getGroups() {
	return groups;
    }

    public void setGroups(List<Group> groups) {
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

    public String getPID() {
	return PID;
    }

    public void setPID(String pID) {
	PID = pID;
    }

    public String getColorCode() {
	return colorCode;
    }

    public void setColorCode(String colorCode) {
	this.colorCode = colorCode;
    }

    public String getThumbnailUrl() {
	return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
	this.thumbnailUrl = thumbnailUrl;
    }

    public Reference getReferredBy() {
	return referredBy;
    }

    public void setReferredBy(Reference referredBy) {
	this.referredBy = referredBy;
    }

	public Boolean getIsPartOfClinic() {
		return isPartOfClinic;
	}

	public void setIsPartOfClinic(Boolean isPartOfClinic) {
		this.isPartOfClinic = isPartOfClinic;
	}

	@Override
	public String toString() {
		return "RegisteredPatientDetails [firstName=" + firstName + ", lastName=" + lastName + ", middleName="
				+ middleName + ", imageUrl=" + imageUrl + ", thumbnailUrl=" + thumbnailUrl + ", dob=" + dob
				+ ", userId=" + userId + ", userName=" + userName + ", mobileNumber=" + mobileNumber + ", gender="
				+ gender + ", patient=" + patient + ", address=" + address + ", groups=" + groups + ", doctorId="
				+ doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", PID=" + PID
				+ ", colorCode=" + colorCode + ", referredBy=" + referredBy + ", isPartOfClinic=" + isPartOfClinic
				+ "]";
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

}
