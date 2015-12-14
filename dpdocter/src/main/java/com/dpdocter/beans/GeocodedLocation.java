package com.dpdocter.beans;

public class GeocodedLocation {
    private String formattedAddress;

    private double latitude;

    private double longitude;

    public String getFormattedAddress() {
	return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
	this.formattedAddress = formattedAddress;
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
	return "GeocodedLocation [formattedAddress=" + formattedAddress + ", latitude=" + latitude + ", longitude=" + longitude + "]";
    }

}
