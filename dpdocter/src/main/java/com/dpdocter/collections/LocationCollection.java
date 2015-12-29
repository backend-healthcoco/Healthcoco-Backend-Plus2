package com.dpdocter.collections;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.WorkingSchedule;

@Document(collection = "location_cl")
public class LocationCollection extends GenericCollection {

    @Id
    private String id;

    @Field
    private String locationName;

    @Field
    private String country;

    @Field
    private String state;

    @Field
    private String city;

    @Field
    private String locationPhoneNumber;

    @Field
    private String postalCode;

    @Field
    private String websiteUrl;

    @Field
    private List<ClinicImage> images;

    @Field
    private String logoUrl;

    @Field
    private String logoThumbnailUrl;

    @Field
    private String hospitalId;

    @Field
    private Double latitude;

    @Field
    private Double longitude;

    @Field
    private String tagLine;

    @Field
    private String landmarkDetails;

    @Field
    private String locationEmailAddress;

    @Field
    private List<String> specialization;

    @Field
    private String streetAddress;

    @Field
    private String locality;

    @Field
    private String mobileNumber;

    @Field
    private List<String> alternateNumbers;

    @Field
    private List<WorkingSchedule> workingSchedules;

    @Field
    private boolean isTwentyFourSevenOpen;

    @Field
    private Boolean isLab = false;
    
    @Field
    private Boolean isOnlineReportsAvailable = false;
    
    @Field
    private Boolean isNABLAccredited = false;
    
    @Field
    private Boolean isHomeServiceAvailable = false;
    
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

    public String getLocationPhoneNumber() {
	return locationPhoneNumber;
    }

    public void setLocationPhoneNumber(String locationPhoneNumber) {
	this.locationPhoneNumber = locationPhoneNumber;
    }

    public String getPostalCode() {
	return postalCode;
    }

    public void setPostalCode(String postalCode) {
	this.postalCode = postalCode;
    }

    public String getWebsiteUrl() {
	return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
	this.websiteUrl = websiteUrl;
    }

    public List<ClinicImage> getImages() {
	return images;
    }

    public void setImages(List<ClinicImage> images) {
	this.images = images;
    }

    public String getLogoUrl() {
	return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
	this.logoUrl = logoUrl;
    }

    public String getHospitalId() {
	return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
	this.hospitalId = hospitalId;
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

    public String getTagLine() {
	return tagLine;
    }

    public void setTagLine(String tagLine) {
	this.tagLine = tagLine;
    }

    public String getLandmarkDetails() {
	return landmarkDetails;
    }

    public void setLandmarkDetails(String landmarkDetails) {
	this.landmarkDetails = landmarkDetails;
    }

    public String getLocationEmailAddress() {
	return locationEmailAddress;
    }

    public void setLocationEmailAddress(String locationEmailAddress) {
	this.locationEmailAddress = locationEmailAddress;
    }

    public List<String> getSpecialization() {
	return specialization;
    }

    public void setSpecialization(List<String> specialization) {
	this.specialization = specialization;
    }

    public String getStreetAddress() {
	return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
	this.streetAddress = streetAddress;
    }

    public String getLocality() {
	return locality;
    }

    public void setLocality(String locality) {
	this.locality = locality;
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

    public List<WorkingSchedule> getWorkingSchedules() {
	return workingSchedules;
    }

    public void setWorkingSchedules(List<WorkingSchedule> workingSchedules) {
	this.workingSchedules = workingSchedules;
    }

    public boolean isTwentyFourSevenOpen() {
	return isTwentyFourSevenOpen;
    }

    public void setTwentyFourSevenOpen(boolean isTwentyFourSevenOpen) {
	this.isTwentyFourSevenOpen = isTwentyFourSevenOpen;
    }

    public String getLogoThumbnailUrl() {
	return logoThumbnailUrl;
    }

    public void setLogoThumbnailUrl(String logoThumbnailUrl) {
	this.logoThumbnailUrl = logoThumbnailUrl;
    }

	public Boolean getIsLab() {
		return isLab;
	}

	public void setIsLab(Boolean isLab) {
		this.isLab = isLab;
	}

	public Boolean getIsOnlineReportsAvailable() {
		return isOnlineReportsAvailable;
	}

	public void setIsOnlineReportsAvailable(Boolean isOnlineReportsAvailable) {
		this.isOnlineReportsAvailable = isOnlineReportsAvailable;
	}

	public Boolean getIsNABLAccredited() {
		return isNABLAccredited;
	}

	public void setIsNABLAccredited(Boolean isNABLAccredited) {
		this.isNABLAccredited = isNABLAccredited;
	}

	public Boolean getIsHomeServiceAvailable() {
		return isHomeServiceAvailable;
	}

	public void setIsHomeServiceAvailable(Boolean isHomeServiceAvailable) {
		this.isHomeServiceAvailable = isHomeServiceAvailable;
	}

	@Override
	public String toString() {
		return "LocationCollection [id=" + id + ", locationName=" + locationName + ", country=" + country + ", state="
				+ state + ", city=" + city + ", locationPhoneNumber=" + locationPhoneNumber + ", postalCode="
				+ postalCode + ", websiteUrl=" + websiteUrl + ", images=" + images + ", logoUrl=" + logoUrl
				+ ", logoThumbnailUrl=" + logoThumbnailUrl + ", hospitalId=" + hospitalId + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", tagLine=" + tagLine + ", landmarkDetails=" + landmarkDetails
				+ ", locationEmailAddress=" + locationEmailAddress + ", specialization=" + specialization
				+ ", streetAddress=" + streetAddress + ", locality=" + locality + ", mobileNumber=" + mobileNumber
				+ ", alternateNumbers=" + alternateNumbers + ", workingSchedules=" + workingSchedules
				+ ", isTwentyFourSevenOpen=" + isTwentyFourSevenOpen + ", isLab=" + isLab
				+ ", isOnlineReportsAvailable=" + isOnlineReportsAvailable + ", isNABLAccredited=" + isNABLAccredited
				+ ", isHomeServiceAvailable=" + isHomeServiceAvailable + "]";
	}
}
