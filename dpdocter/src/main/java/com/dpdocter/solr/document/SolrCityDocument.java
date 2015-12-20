package com.dpdocter.solr.document;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.geo.GeoLocation;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "cities")
public class SolrCityDocument {

    @Id
    @Field
    private String id;

    @Field
    private String city;

    @Field
    private Boolean isActivated = false;

    @Field
    private String description;

    @Field
    private String countryId;

    @SuppressWarnings("deprecation")
    @Field
    private GeoLocation geoLocation;

    @Field
    private double latitude;

    @Field
    private double longitude;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getCity() {
	return city;
    }

    public void setCity(String city) {
	this.city = city;
    }

    public Boolean getIsActivated() {
	return isActivated;
    }

    public void setIsActivated(Boolean isActivated) {
	this.isActivated = isActivated;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getCountryId() {
	return countryId;
    }

    public void setCountryId(String countryId) {
	this.countryId = countryId;
    }

    public GeoLocation getGeoLocation() {
	return geoLocation;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
	this.geoLocation = geoLocation;
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
	return "SolrCityDocument [id=" + id + ", city=" + city + ", isActivated=" + isActivated + ", description=" + description + ", countryId=" + countryId
		+ ", geoLocation=" + geoLocation + ", latitude=" + latitude + ", longitude=" + longitude + "]";
    }
}
