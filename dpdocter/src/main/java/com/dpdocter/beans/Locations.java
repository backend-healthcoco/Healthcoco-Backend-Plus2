package com.dpdocter.beans;
/**
 * @author veeraj
 */

public class Locations {
	private String id;
	private String locationName;
	private String country;
	private String state;
	private String city;
	private String locationPhoneNumber;
	private String postalCode;
	private String websiteUrl;
	private String imageUrl;
	private String hospitalId;
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
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
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
	@Override
	public String toString() {
		return "Locations [id=" + id + ", locationName=" + locationName
				+ ", country=" + country + ", state=" + state + ", city="
				+ city + ", locationPhoneNumber=" + locationPhoneNumber
				+ ", postalCode=" + postalCode + ", websiteUrl=" + websiteUrl
				+ ", imageUrl=" + imageUrl + ", hospitalId=" + hospitalId
				+ ", latitude=" + latitude + ", longitude=" + longitude + "]";
	}
	
	
	
	
}
