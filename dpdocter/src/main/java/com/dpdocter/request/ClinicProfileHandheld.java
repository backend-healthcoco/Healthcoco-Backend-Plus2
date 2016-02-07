package com.dpdocter.request;

import java.util.List;

public class ClinicProfileHandheld {

    private String id;

    private String websiteUrl;

    private String locationEmailAddress;

    private String mobileNumber;

    private List<String> alternateNumbers;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getWebsiteUrl() {
	return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
	this.websiteUrl = websiteUrl;
    }

    public String getLocationEmailAddress() {
	return locationEmailAddress;
    }

    public void setLocationEmailAddress(String locationEmailAddress) {
	this.locationEmailAddress = locationEmailAddress;
    }

    public String getMobileNumber() {
	return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
	this.mobileNumber = mobileNumber;
    }

    public List<String> getAlternateNumbers() {
	return alternateNumbers;
    }

    public void setAlternateNumbers(List<String> alternateNumbers) {
	this.alternateNumbers = alternateNumbers;
    }

    @Override
    public String toString() {
	return "ClinicProfileHandheld [id=" + id + ", websiteUrl=" + websiteUrl + ", locationEmailAddress=" + locationEmailAddress + ", mobileNumber="
		+ mobileNumber + ", alternateNumbers=" + alternateNumbers + "]";
    }
}
