package com.dpdocter.solr.document;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "locations")
public class SolrLocationDocument {
    @Id
    @Field
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
    private String postalCode;

    @Field
    private String websiteUrl;

    @Field
    private String imageUrl;

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
    private String clinicNumber;

    @Field
    private List<String> alternateClinicNumbers;

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

    public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
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
		return "SolrLocationDocument [id=" + id + ", locationName=" + locationName + ", country=" + country + ", state="
				+ state + ", city=" + city + ", postalCode=" + postalCode + ", websiteUrl=" + websiteUrl + ", imageUrl="
				+ imageUrl + ", latitude=" + latitude + ", longitude=" + longitude + ", landmarkDetails="
				+ landmarkDetails + ", locationEmailAddress=" + locationEmailAddress + ", streetAddress="
				+ streetAddress + ", locality=" + locality + ", clinicNumber=" + clinicNumber
				+ ", alternateClinicNumbers=" + alternateClinicNumbers + "]";
	}
}
