package com.dpdocter.elasticsearch.beans;

public class ESCityLandmarkLocalityResponse {

	private String id;

	private String country;

	private String state;

	private String city;

	private String locality;

	private String landmark;

	private double latitude;

	private double longitude;

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

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
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
		return "ESCityLandmarkLocalityResponse [id=" + id + ", country=" + country + ", state=" + state + ", city="
				+ city + ", locality=" + locality + ", landmark=" + landmark + ", latitude=" + latitude + ", longitude="
				+ longitude + "]";
	}
}
