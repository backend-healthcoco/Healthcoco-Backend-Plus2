package com.dpdocter.elasticsearch.response;

import java.util.List;

import com.dpdocter.beans.LabTest;

public class LabResponse {

    private String locationId;

    private String locationName;

    private String country;

    private String state;

    private String city;

    private String postalCode;

    private String websiteUrl;

    private Double latitude;

    private Double longitude;

    private String landmarkDetails;

    private String locationEmailAddress;

    private String streetAddress;

    private String locality;

    private String mobileNumber;

    private String alternateNumber;

    private List<String> specialization;

    private Boolean isLab = false;

    private Boolean isOnlineReportsAvailable = false;

    private Boolean isNABLAccredited = false;

    private Boolean isHomeServiceAvailable = false;

    private LabTest labTest;

    private List<String> images;

    private String logoUrl;

    private Integer noOfReviews = 0;

    private Integer noOfRecommenations = 0;

    private Double distance;

    private String clinicNumber;

    private String clinicAddress;
    
	private int noOfClinicRecommendations = 0;
	
	private Integer noOfClinicReview = 0;

    private String googleMapShortUrl;
	
    public Integer getNoOfClinicReview() {
		return noOfClinicReview;
	}

	public void setNoOfClinicReview(Integer noOfClinicReview) {
		this.noOfClinicReview = noOfClinicReview;
	}

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

    public String getAlternateNumber() {
	return alternateNumber;
    }

    public void setAlternateNumber(String alternateNumber) {
	this.alternateNumber = alternateNumber;
    }

    public List<String> getSpecialization() {
	return specialization;
    }

    public void setSpecialization(List<String> specialization) {
	this.specialization = specialization;
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

    public LabTest getLabTest() {
	return labTest;
    }

    public void setLabTest(LabTest labTest) {
	this.labTest = labTest;
    }

    public List<String> getImages() {
	return images;
    }

    public void setImages(List<String> images) {
	this.images = images;
    }

    public String getLogoUrl() {
	return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
	this.logoUrl = logoUrl;
    }

	public Integer getNoOfReviews() {
		return noOfReviews;
	}

	public void setNoOfReviews(Integer noOfReviews) {
		this.noOfReviews = noOfReviews;
	}

	public Integer getNoOfRecommenations() {
		return noOfRecommenations;
	}

	public void setNoOfRecommenations(Integer noOfRecommenations) {
		this.noOfRecommenations = noOfRecommenations;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public String getClinicNumber() {
		return clinicNumber;
	}

	public void setClinicNumber(String clinicNumber) {
		this.clinicNumber = clinicNumber;
	}

	public String getClinicAddress() {
		return clinicAddress;
	}

	public void setClinicAddress(String clinicAddress) {
		this.clinicAddress = clinicAddress;
	}

	public int getNoOfClinicRecommendations() {
		return noOfClinicRecommendations;
	}

	public void setNoOfClinicRecommendations(int noOfClinicRecommendations) {
		this.noOfClinicRecommendations = noOfClinicRecommendations;
	}

	public String getGoogleMapShortUrl() {
		return googleMapShortUrl;
	}

	public void setGoogleMapShortUrl(String googleMapShortUrl) {
		this.googleMapShortUrl = googleMapShortUrl;
	}

	@Override
	public String toString() {
		return "LabResponse [locationId=" + locationId + ", locationName=" + locationName + ", country=" + country
				+ ", state=" + state + ", city=" + city + ", postalCode=" + postalCode + ", websiteUrl=" + websiteUrl
				+ ", latitude=" + latitude + ", longitude=" + longitude + ", landmarkDetails=" + landmarkDetails
				+ ", locationEmailAddress=" + locationEmailAddress + ", streetAddress=" + streetAddress + ", locality="
				+ locality + ", mobileNumber=" + mobileNumber + ", alternateNumber=" + alternateNumber
				+ ", specialization=" + specialization + ", isLab=" + isLab + ", isOnlineReportsAvailable="
				+ isOnlineReportsAvailable + ", isNABLAccredited=" + isNABLAccredited + ", isHomeServiceAvailable="
				+ isHomeServiceAvailable + ", labTest=" + labTest + ", images=" + images + ", logoUrl=" + logoUrl
				+ ", noOfReviews=" + noOfReviews + ", noOfRecommenations=" + noOfRecommenations + ", distance="
				+ distance + ", clinicNumber=" + clinicNumber + ", clinicAddress=" + clinicAddress
				+ ", noOfClinicRecommendations=" + noOfClinicRecommendations + ", noOfClinicReview=" + noOfClinicReview
				+ ", googleMapShortUrl=" + googleMapShortUrl + "]";
	}

}
