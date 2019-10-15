package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ClinicProfile {

    private String id;

    private String locationName;

    private String tagLine;

    private List<String> specialization;

    private String locationEmailAddress;

    private String websiteUrl;

    private String landmarkDetails;

    private String clinicNumber;

    private List<String> alternateClinicNumbers;

    private String googleMapShortUrl;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getLocationName() {
	return locationName;
    }

    public void setLocationName(String locationName) {
	this.locationName = locationName;
    }

    public String getTagLine() {
	return tagLine;
    }

    public void setTagLine(String tagLine) {
	this.tagLine = tagLine;
    }

    public List<String> getSpecialization() {
	return specialization;
    }

    public void setSpecialization(List<String> specialization) {
	this.specialization = specialization;
    }

    public String getLocationEmailAddress() {
	return locationEmailAddress;
    }

    public void setLocationEmailAddress(String locationEmailAddress) {
	this.locationEmailAddress = locationEmailAddress;
    }

    public String getWebsiteUrl() {
	return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
	this.websiteUrl = websiteUrl;
    }

    public String getLandmarkDetails() {
	return landmarkDetails;
    }

    public void setLandmarkDetails(String landmarkDetails) {
	this.landmarkDetails = landmarkDetails;
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

	public String getGoogleMapShortUrl() {
		return googleMapShortUrl;
	}

	public void setGoogleMapShortUrl(String googleMapShortUrl) {
		this.googleMapShortUrl = googleMapShortUrl;
	}

	@Override
	public String toString() {
		return "ClinicProfile [id=" + id + ", locationName=" + locationName + ", tagLine=" + tagLine
				+ ", specialization=" + specialization + ", locationEmailAddress=" + locationEmailAddress
				+ ", websiteUrl=" + websiteUrl + ", landmarkDetails=" + landmarkDetails + ", clinicNumber="
				+ clinicNumber + ", alternateClinicNumbers=" + alternateClinicNumbers + ", googleMapShortUrl="
				+ googleMapShortUrl + "]";
	}

}
