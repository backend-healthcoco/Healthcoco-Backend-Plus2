package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class City {

    private String id;

    private String city;

    private String explanation;

    private Boolean isActivated = true;

    private String state;

    private String country;

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
				+ ", state=" + state + ", country=" + country + ", latitude=" + latitude + ", longitude=" + longitude
				+ "]";
	}
}
