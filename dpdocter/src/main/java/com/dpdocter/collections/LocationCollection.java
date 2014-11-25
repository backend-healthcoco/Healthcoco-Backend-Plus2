package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="location_cl")
public class LocationCollection {
	
	@Id
	private String id;
	@Field
	private String name;
	@Field
	private String country;
	@Field
	private String state;
	@Field
	private String city;
	@Field
	private String phoneNumber;
	@Field
	private String postalCode;
	@Field
	private String websiteUrl;
	@Field
	private String imageUrl;
	@Field
	private String hospitalId;
	@Field
	private Double latitude;
	@Field
	private Double longitude;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
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
		return "LocationCollection [id=" + id + ", name=" + name + ", country="
				+ country + ", state=" + state + ", city=" + city
				+ ", phoneNumber=" + phoneNumber + ", postalCode=" + postalCode
				+ ", websiteUrl=" + websiteUrl + ", imageUrl=" + imageUrl
				+ ", hospitalId=" + hospitalId + ", latitude=" + latitude
				+ ", longitude=" + longitude + "]";
	}
	
	
	
	
}
