package com.dpdocter.request;

import java.util.List;

public class ClinicProfileHandheld {

    private String id;

    private String websiteUrl;

    private String locationEmailAddress;

    private String clinicNumber;

    private List<String> alternateClinicNumbers;

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

	public String getClinicNumber() {
		return clinicNumber;
	}

	public void setClinicNumber(String clinicNumber) {
		this.clinicNumber = clinicNumber;
	}

	public List<String> getAlternateClinicNumbers() {
		return alternateClinicNumbers;
	}

	public void setAlternateClinicNumbers(List<String> alternateClinicNumbers) {
		this.alternateClinicNumbers = alternateClinicNumbers;
	}

	@Override
	public String toString() {
		return "ClinicProfileHandheld [id=" + id + ", websiteUrl=" + websiteUrl + ", locationEmailAddress="
				+ locationEmailAddress + ", clinicNumber=" + clinicNumber + ", alternateClinicNumbers="
				+ alternateClinicNumbers + "]";
	}
}
