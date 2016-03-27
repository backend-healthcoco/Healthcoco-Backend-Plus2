package com.dpdocter.beans;

public class City {

    private String id;

    private String city;

    private String explanation;

    private Boolean isActivated = true;

    private String stateId;

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

    public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public Boolean getIsActivated() {
	return isActivated;
    }

    public void setIsActivated(Boolean isActivated) {
	this.isActivated = isActivated;
    }

    public String getStateId() {
	return stateId;
    }

    public void setStateId(String stateId) {
	this.stateId = stateId;
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
		return "City [id=" + id + ", city=" + city + ", explanation=" + explanation + ", isActivated=" + isActivated
				+ ", stateId=" + stateId + ", latitude=" + latitude + ", longitude=" + longitude + "]";
	}

}
