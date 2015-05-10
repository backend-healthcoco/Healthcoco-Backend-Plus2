package com.dpdocter.beans;

import java.util.List;

public class ClinicProfile {

	private String locationId;
	private String locationName;
	private String tagLine;
	private List<String> specialization;
	private String locationEmailAddress;
	private String websiteUrl;
	private String landmarkDetails;

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
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

	@Override
	public String toString() {
		return "ClinicProfile [locationId=" + locationId + ", locationName=" + locationName + ", tagLine=" + tagLine + ", specialization=" + specialization
				+ ", locationEmailAddress=" + locationEmailAddress + ", websiteUrl=" + websiteUrl + ", landmarkDetails=" + landmarkDetails + "]";
	}

}
