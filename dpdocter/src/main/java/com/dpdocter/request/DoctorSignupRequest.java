package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.DOB;
import com.dpdocter.beans.FileDetails;

/**
 * @author veeraj
 */
public class DoctorSignupRequest {
    // user details
    private String firstName;

    private String userName;

    private String password;

    private String emailAddress;

    private String mobileNumber;

    private FileDetails image;

    private String gender;

    private DOB dob;

    // doctor details
    private String phoneNumber;

    private String imageUrl;

    // doctor details
    private List<String> specialization;

    // hospital details
    private String hospitalName;

    private String hospitalPhoneNumber;

    private String hospitalImageUrl;

    private String hospitalDescription;

    // location details
    private String locationName;

    private String country;

    private String state;

    private String city;

    private String locationPhoneNumber;

    private String postalCode;

    private String websiteUrl;

    private String locationImageUrl;

    private String hospitalId;

    private Double latitude;

    private Double longitude;

    private String streetAddress;

    public DOB getDob() {
	return dob;
    }

    public void setDob(DOB dob) {
	this.dob = dob;
    }

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

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public String getEmailAddress() {
	return emailAddress != null ? emailAddress.toLowerCase() : emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
	this.emailAddress = emailAddress;
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

    public String getPhoneNumber() {
	return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
	this.phoneNumber = phoneNumber;
    }

    public List<String> getSpecialization() {
	return specialization;
    }

    public void setSpecialization(List<String> specialization) {
	this.specialization = specialization;
    }

    public String getHospitalName() {
	return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
	this.hospitalName = hospitalName;
    }

    public String getHospitalPhoneNumber() {
	return hospitalPhoneNumber;
    }

    public void setHospitalPhoneNumber(String hospitalPhoneNumber) {
	this.hospitalPhoneNumber = hospitalPhoneNumber;
    }

    public String getHospitalImageUrl() {
	return hospitalImageUrl;
    }

    public void setHospitalImageUrl(String hospitalImageUrl) {
	this.hospitalImageUrl = hospitalImageUrl;
    }

    public String getHospitalDescription() {
	return hospitalDescription;
    }

    public void setHospitalDescription(String hospitalDescription) {
	this.hospitalDescription = hospitalDescription;
    }

    public String getLocationName() {
	return locationName;
    }

    public void setLocationName(String locationName) {
	this.locationName = locationName;
    }

    public String getCountry() {
	return country;
    }

    public void setCountry(String country) {
	this.country = country;
    }

    public String getState() {
	return state;
    }

    public void setState(String state) {
	this.state = state;
    }

    public String getCity() {
	return city;
    }

    public void setCity(String city) {
	this.city = city;
    }

    public String getLocationPhoneNumber() {
	return locationPhoneNumber;
    }

    public void setLocationPhoneNumber(String locationPhoneNumber) {
	this.locationPhoneNumber = locationPhoneNumber;
    }

    public String getPostalCode() {
	return postalCode;
    }

    public void setPostalCode(String postalCode) {
	this.postalCode = postalCode;
    }

    public String getWebsiteUrl() {
	return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
	this.websiteUrl = websiteUrl;
    }

    public String getLocationImageUrl() {
	return locationImageUrl;
    }

    public void setLocationImageUrl(String locationImageUrl) {
	this.locationImageUrl = locationImageUrl;
    }

    public String getHospitalId() {
	return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
	this.hospitalId = hospitalId;
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

    public FileDetails getImage() {
	return image;
    }

    public void setImage(FileDetails image) {
	this.image = image;
    }

    public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
    }

    public String getStreetAddress() {
	return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
	this.streetAddress = streetAddress;
    }

    @Override
    public String toString() {
	return "DoctorSignupRequest [firstName=" + firstName + ", userName=" + userName
		+ ", password=" + password + ", emailAddress=" + emailAddress + ", mobileNumber=" + mobileNumber + ", image=" + image + ", gender=" + gender
		+ ", dob=" + dob + ", phoneNumber=" + phoneNumber + ", imageUrl=" + imageUrl + ", specialization=" + specialization + ", hospitalName="
		+ hospitalName + ", hospitalPhoneNumber=" + hospitalPhoneNumber + ", hospitalImageUrl=" + hospitalImageUrl + ", hospitalDescription="
		+ hospitalDescription + ", locationName=" + locationName + ", country=" + country + ", state=" + state + ", city=" + city
		+ ", locationPhoneNumber=" + locationPhoneNumber + ", postalCode=" + postalCode + ", websiteUrl=" + websiteUrl + ", locationImageUrl="
		+ locationImageUrl + ", hospitalId=" + hospitalId + ", latitude=" + latitude + ", longitude=" + longitude + ", streetAddress=" + streetAddress
		+ "]";
    }

}
