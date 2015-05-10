package com.dpdocter.beans;

public class ClinicAddress {
	private String locationId;
	private String streetAddress;
	private String locality;
	private String city;
	private String state;
	private String country;
	private String postalCode;
	private String mobileNumber;
	private String locationPhoneNumber;
	private String alternateNumber;

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
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

	public String getAlternateNumber() {
		return alternateNumber;
	}

	public void setAlternateNumber(String alternateNumber) {
		this.alternateNumber = alternateNumber;
	}

	@Override
	public String toString() {
		return "ClinicAddress [locationId=" + locationId + ", streetAddress=" + streetAddress + ", locality=" + locality + ", city=" + city + ", state="
				+ state + ", country=" + country + ", postalCode=" + postalCode + ", mobileNumber=" + mobileNumber + ", locationPhoneNumber="
				+ locationPhoneNumber + ", alternateNumber=" + alternateNumber + "]";
	}

}
