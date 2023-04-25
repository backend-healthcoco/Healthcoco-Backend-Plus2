package com.dpdocter.request;

import java.util.List;

public class DoctorSignupHandheldContinueRequest {

	private String userId;

	private List<String> specialization;

	private String hospitalName;

	private String hospitalPhoneNumber;

	private String hospitalImageUrl;

	private String hospitalDescription;

	private String locationName;

	private String country;

	private String state;

	private String city;

	private String clinicNumber;

	private String postalCode;

	private String websiteUrl;

	private String locationImageUrl;

	private String hospitalId;

	private Double latitude;

	private Double longitude;

	private String streetAddress;

	private String registerNumber;
	private String googleMapShortUrl;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getClinicNumber() {
		return clinicNumber;
	}

	public void setClinicNumber(String clinicNumber) {
		this.clinicNumber = clinicNumber;
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

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getRegisterNumber() {
		return registerNumber;
	}

	public void setRegisterNumber(String registerNumber) {
		this.registerNumber = registerNumber;
	}

	public String getGoogleMapShortUrl() {
		return googleMapShortUrl;
	}

	public void setGoogleMapShortUrl(String googleMapShortUrl) {
		this.googleMapShortUrl = googleMapShortUrl;
	}

	@Override
	public String toString() {
		return "DoctorSignupHandheldContinueRequest [userId=" + userId + ", specialization=" + specialization
				+ ", hospitalName=" + hospitalName + ", hospitalPhoneNumber=" + hospitalPhoneNumber
				+ ", hospitalImageUrl=" + hospitalImageUrl + ", hospitalDescription=" + hospitalDescription
				+ ", locationName=" + locationName + ", country=" + country + ", state=" + state + ", city=" + city
				+ ", clinicNumber=" + clinicNumber + ", postalCode=" + postalCode + ", websiteUrl=" + websiteUrl
				+ ", locationImageUrl=" + locationImageUrl + ", hospitalId=" + hospitalId + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", streetAddress=" + streetAddress + ", registerNumber=" + registerNumber
				+ ", googleMapShortUrl=" + googleMapShortUrl + "]";
	}

}
