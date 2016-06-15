package com.dpdocter.beans;

/**
 * @author veeraj
 */
public class Address {

    private String country;

    private String city;

    private String state;

    private String postalCode;

    private String locality;

    private Double latitude;

    private Double longitude;

    private String streetAddress;
    
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
