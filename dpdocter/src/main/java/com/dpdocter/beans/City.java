package com.dpdocter.beans;

public class City {

    private String id;

    private String city;

    private String description;

    private Boolean isActivated = false;

    private String countryId;

    private double latitude;

    private double longitude;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getCity() {
	return city;
    }

    public void setCity(String city) {
	this.city = city;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public Boolean getIsActivated() {
	return isActivated;
    }

    public void setIsActivated(Boolean isActivated) {
	this.isActivated = isActivated;
    }

    public String getCountryId() {
	return countryId;
    }

    public void setCountryId(String countryId) {
	this.countryId = countryId;
    }

    public double getLatitude() {
	return latitude;
    }

    public void setLatitude(double latitude) {
	this.latitude = latitude;
    }

    public double getLongitude() {
	return longitude;
    }

    public void setLongitude(double longitude) {
	this.longitude = longitude;
    }

    @Override
    public String toString() {
	return "City [id=" + id + ", city=" + city + ", description=" + description + ", isActivated=" + isActivated + ", countryId=" + countryId
		+ ", latitude=" + latitude + ", longitude=" + longitude + "]";
    }
}
