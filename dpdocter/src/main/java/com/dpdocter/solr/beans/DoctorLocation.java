package com.dpdocter.solr.beans;

import org.apache.solr.client.solrj.beans.Field;

public class DoctorLocation {
    @Field
    private String locationName;

    @Field
    private String country;

    @Field
    private String state;

    @Field
    private String city;

    @Field
    private String locationPhoneNumber;

    @Field
    private String postalCode;

    @Field
    private String websiteUrl;

    @Field
    private Double latitude;

    @Field
    private Double longitude;

    @Field
    private String landmarkDetails;

    @Field
    private String locationEmailAddress;

    @Field
    private String streetAddress;

    @Field
    private String locality;

    @Field
    private String mobileNumber;

    @Field
    private String alternateNumber;

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

    public String getLandmarkDetails() {
	return landmarkDetails;
    }

    public void setLandmarkDetails(String landmarkDetails) {
	this.landmarkDetails = landmarkDetails;
    }

    public String getLocationEmailAddress() {
	return locationEmailAddress;
    }

    public void setLocationEmailAddress(String locationEmailAddress) {
	this.locationEmailAddress = locationEmailAddress;
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

    public String getMobileNumber() {
	return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
	this.mobileNumber = mobileNumber;
    }

    public String getAlternateNumber() {
	return alternateNumber;
    }

    public void setAlternateNumber(String alternateNumber) {
	this.alternateNumber = alternateNumber;
    }

    @Override
    public String toString() {
	return "DoctorLocation [locationName=" + locationName + ", country=" + country + ", state=" + state + ", city=" + city + ", locationPhoneNumber="
		+ locationPhoneNumber + ", postalCode=" + postalCode + ", websiteUrl=" + websiteUrl + ", latitude=" + latitude + ", longitude=" + longitude
		+ ", landmarkDetails=" + landmarkDetails + ", locationEmailAddress=" + locationEmailAddress + ", streetAddress=" + streetAddress
		+ ", locality=" + locality + ", mobileNumber=" + mobileNumber + ", alternateNumber=" + alternateNumber + "]";
    }

}
