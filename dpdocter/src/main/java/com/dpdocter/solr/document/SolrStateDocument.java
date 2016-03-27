package com.dpdocter.solr.document;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.geo.GeoLocation;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "states")
public class SolrStateDocument {

    @Id
    @Field
    private String id;

    @Field
    private String state;

    @Field
    private Boolean isActivated = false;

    @Field
    private String explanation;

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

    public String getState() {
	return state;
    }

    public void setState(String state) {
	this.state = state;
    }

    public Boolean getIsActivated() {
	return isActivated;
    }

    public void setIsActivated(Boolean isActivated) {
	this.isActivated = isActivated;
    }

    public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
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
		return "SolrStateDocument [id=" + id + ", state=" + state + ", isActivated=" + isActivated + ", explanation="
				+ explanation + ", countryId=" + countryId + ", geoLocation=" + geoLocation + ", latitude=" + latitude
				+ ", longitude=" + longitude + "]";
	}

}
