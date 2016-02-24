package com.dpdocter.solr.beans;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.geo.GeoLocation;

public class DoctorLocation {
    @Field
    private String locationId;

    @Field
    private String hospitalId;

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
    private GeoLocation geoLocation;

    @Field
    private Double latitude;

    @Field
    private Double longitude;

    @Field
    private String landmarkDetails;

    @Field
    private String locationEmailAddress;

    @Field
    private String streetAddress;

    @Field
    private String locality;

    @Field
    private String locationMobileNumber;

    @Field
    private List<String> alternateNumbers;

    @Field
    private List<String> specialization;

    @Field
    private Boolean isClinic = true;
    
    @Field
    private Boolean isLab = false;

    @Field
    private Boolean isOnlineReportsAvailable = false;

    @Field
    private Boolean isNABLAccredited = false;

    @Field
    private Boolean isHomeServiceAvailable = false;
    
    @Field
    private List<String> images;

    @Field
    private String logoUrl;

    @Field
    private Integer noOfReviews = 0;

    @Field
    private Integer noOfRecommenations = 0;

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

    public String getLocationMobileNumber() {
	return locationMobileNumber;
    }

    public void setLocationMobileNumber(String locationMobileNumber) {
	this.locationMobileNumber = locationMobileNumber;
    }

    public List<String> getAlternateNumbers() {
		return alternateNumbers;
	}

	public void setAlternateNumbers(List<String> alternateNumbers) {
		this.alternateNumbers = alternateNumbers;
	}

	public String getLocationId() {
	return locationId;
    }

    public void setLocationId(String locationId) {
	this.locationId = locationId;
    }

    public List<String> getSpecialization() {
	return specialization;
    }

    public void setSpecialization(List<String> specialization) {
	this.specialization = specialization;
    }

	public GeoLocation getGeoLocation() {
		return geoLocation;
	}

	public void setGeoLocation(GeoLocation geoLocation) {
		this.geoLocation = geoLocation;
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

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
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

	public Boolean getIsClinic() {
		return isClinic;
	}

	public void setIsClinic(Boolean isClinic) {
		this.isClinic = isClinic;
	}

	@Override
	public String toString() {
		return "DoctorLocation [locationId=" + locationId + ", hospitalId=" + hospitalId + ", locationName="
				+ locationName + ", country=" + country + ", state=" + state + ", city=" + city
				+ ", locationPhoneNumber=" + locationPhoneNumber + ", postalCode=" + postalCode + ", websiteUrl="
				+ websiteUrl + ", geoLocation=" + geoLocation + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", landmarkDetails=" + landmarkDetails + ", locationEmailAddress=" + locationEmailAddress
				+ ", streetAddress=" + streetAddress + ", locality=" + locality + ", locationMobileNumber="
				+ locationMobileNumber + ", alternateNumbers=" + alternateNumbers + ", specialization=" + specialization
				+ ", isClinic=" + isClinic + ", isLab=" + isLab + ", isOnlineReportsAvailable="
				+ isOnlineReportsAvailable + ", isNABLAccredited=" + isNABLAccredited + ", isHomeServiceAvailable="
				+ isHomeServiceAvailable + ", images=" + images + ", logoUrl=" + logoUrl + ", noOfReviews="
				+ noOfReviews + ", noOfRecommenations=" + noOfRecommenations + "]";
	}	
}
