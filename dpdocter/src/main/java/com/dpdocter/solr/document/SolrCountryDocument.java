package com.dpdocter.solr.document;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.geo.GeoLocation;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "countries")
public class SolrCountryDocument {

	  @Id
	  @Field
	  private String id;

	  @Field
	  private String country;

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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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
		return "SolrCountryDocument [id=" + id + ", country=" + country + ", description=" + description
				+ ", geoLocation=" + geoLocation + ", latitude=" + latitude + ", longitude=" + longitude + "]";
	}
	
}
