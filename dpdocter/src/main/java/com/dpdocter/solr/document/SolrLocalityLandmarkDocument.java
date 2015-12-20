package com.dpdocter.solr.document;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.geo.GeoLocation;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "localitiesAndLandmarks")
public class SolrLocalityLandmarkDocument {

    @Id
    @Field
    private String id;

    @Field
    private String cityId;

    @Field
    private String locality;

    @Field
    private String landmark;

    @Field
    private String description;

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

    public String getCityId() {
	return cityId;
    }

    public void setCityId(String cityId) {
	this.cityId = cityId;
    }

    public String getLocality() {
	return locality;
    }

    public void setLocality(String locality) {
	this.locality = locality;
    }

    public String getLandmark() {
	return landmark;
    }

    public void setLandmark(String landmark) {
	this.landmark = landmark;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
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
	return "SolrLocalityLandmarkDocument [id=" + id + ", cityId=" + cityId + ", locality=" + locality + ", landmark=" + landmark + ", description="
		+ description + ", geoLocation=" + geoLocation + ", latitude=" + latitude + ", longitude=" + longitude + "]";
    }
}
