package com.dpdocter.response;

public class SearchLandmarkLocalityResponse {

	private String city;

	private String name;

	private String slugUrl;

	private double latitude;

	private double longitude;

	private String responseType;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSlugUrl() {
		return slugUrl;
	}

	public void setSlugUrl(String slugUrl) {
		this.slugUrl = slugUrl;
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

	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	@Override
	public String toString() {
		return "SearchLandmarkLocalityResponse [city=" + city + ", name=" + name + ", slugUrl=" + slugUrl
				+ ", latitude=" + latitude + ", longitude=" + longitude + ", responseType=" + responseType + "]";
	}
}
