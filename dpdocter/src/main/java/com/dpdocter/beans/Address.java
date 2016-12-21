package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author veeraj
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Address {

    private String country;

    private String city;

    private String state;

    private String postalCode;

    private String locality;

    private Double latitude;

    private Double longitude;

    private String streetAddress;
    
    public Address() {
		super();
	}

    
	public Address(String country, String city, String state, String postalCode, String locality, Double latitude,
			Double longitude, String streetAddress) {
		super();
		this.country = country;
		this.city = city;
		this.state = state;
		this.postalCode = postalCode;
		this.locality = locality;
		this.latitude = latitude;
		this.longitude = longitude;
		this.streetAddress = streetAddress;
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

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	@Override
	public String toString() {
		return "Address [country=" + country + ", city=" + city + ", state=" + state + ", postalCode=" + postalCode
				+ ", locality=" + locality + ", latitude=" + latitude + ", longitude=" + longitude + ", streetAddress="
				+ streetAddress + "]";
	}
}
