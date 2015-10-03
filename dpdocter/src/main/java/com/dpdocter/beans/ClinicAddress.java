package com.dpdocter.beans;

import java.util.List;

public class ClinicAddress {

    private String id;

    private String locationName;

    private String streetAddress;

    private String locality;

    private String city;

    private String state;

    private String country;

    private String postalCode;

    private String mobileNumber;

    private String locationPhoneNumber;

    private List<String> alternateNumbers;

    private String landmarkDetails;

    private Double latitude;

    private Double longitude;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getLocationName() {
	return locationName;
    }

    public void setLocationName(String locationName) {
	this.locationName = locationName;
    }

    public String getStreetAddress() {
	return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
	this.streetAddress = streetAddress;
    }

    public String getLocality() {
	return locality;
    }

    public void setLocality(String locality) {
	this.locality = locality;
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

    public String getCountry() {
	return country;
    }

    public void setCountry(String country) {
	this.country = country;
    }

    public String getPostalCode() {
	return postalCode;
    }

    public void setPostalCode(String postalCode) {
	this.postalCode = postalCode;
    }

    public String getMobileNumber() {
	return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
	this.mobileNumber = mobileNumber;
    }

    public String getLocationPhoneNumber() {
	return locationPhoneNumber;
    }

    public void setLocationPhoneNumber(String locationPhoneNumber) {
	this.locationPhoneNumber = locationPhoneNumber;
    }

    public List<String> getAlternateNumbers() {
	return alternateNumbers;
    }

    public void setAlternateNumbers(List<String> alternateNumbers) {
	this.alternateNumbers = alternateNumbers;
    }

    public String getLandmarkDetails() {
	return landmarkDetails;
    }

    public void setLandmarkDetails(String landmarkDetails) {
	this.landmarkDetails = landmarkDetails;
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

    @Override
    public String toString() {
	return "ClinicAddress [id=" + id + ", locationName=" + locationName + ", streetAddress=" + streetAddress + ", locality=" + locality + ", city=" + city
		+ ", state=" + state + ", country=" + country + ", postalCode=" + postalCode + ", mobileNumber=" + mobileNumber + ", locationPhoneNumber="
		+ locationPhoneNumber + ", alternateNumbers=" + alternateNumbers + ", landmarkDetails=" + landmarkDetails + ", latitude=" + latitude
		+ ", longitude=" + longitude + "]";
    }
}
