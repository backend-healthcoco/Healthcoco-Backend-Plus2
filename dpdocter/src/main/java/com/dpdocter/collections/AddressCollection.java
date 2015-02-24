package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="address_cl")
public class AddressCollection {

	@Id
	private String id;
	@Field
	private String country;
	@Field
	private String city;
	@Field
	private String state;
	@Field
	private String postalCode;
	@Field
	private String roadNumber;
	@Field
	private String roadName;
	@Field
	private String houseNumber;
	@Field
	private String userId;
	@Field
	private Double latitude;
	@Field
	private Double longitude;
	@Field
	private String locality;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getRoadNumber() {
		return roadNumber;
	}
	public void setRoadNumber(String roadNumber) {
		this.roadNumber = roadNumber;
	}
	public String getRoadName() {
		return roadName;
	}
	public void setRoadName(String roadName) {
		this.roadName = roadName;
	}
	public String getHouseNumber() {
		return houseNumber;
	}
	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
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
	
	
	public String getLocality() {
		return locality;
	}
	public void setLocality(String locality) {
		this.locality = locality;
	}
	@Override
	public String toString() {
		return "AddressCollection [id=" + id + ", country=" + country
				+ ", city=" + city + ", state=" + state + ", postalCode="
				+ postalCode + ", roadNumber=" + roadNumber + ", roadName="
				+ roadName + ", houseNumber=" + houseNumber + ", userId="
				+ userId + ", latitude=" + latitude + ", longitude="
				+ longitude + ", locality=" + locality + "]";
	}
	
	
	
	
}
