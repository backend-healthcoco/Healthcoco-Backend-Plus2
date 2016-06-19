package com.dpdocter.elasticsearch.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Document(indexName = "cities_in", type = "cities")
public class ESCityDocument {

    @Id
    private String id;

    @Field(type = FieldType.String)
    private String city;

    @Field(type = FieldType.Boolean)
    private Boolean isActivated = false;

    @Field(type = FieldType.String)
    private String explanation;

    @Field(type = FieldType.String)
    private String state;

    @Field(type = FieldType.String)
    private String country;

    @Field(type = FieldType.Object)
    @GeoPointField
    private GeoPoint geoPoint;


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

    public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

    public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

    public GeoPoint getGeoPoint() {
		return geoPoint;
	}

	public void setGeoPoint(GeoPoint geoPoint) {
		this.geoPoint = geoPoint;
	}

	public double getLatitude() {
	return latitude;
    }

    public void setLatitude(double latitude) {
	this.latitude = latitude;
	geoPoint = new GeoPoint(latitude, longitude);
    }

    public double getLongitude() {
	return longitude;
    }

    public void setLongitude(double longitude) {
	this.longitude = longitude;
	geoPoint = new GeoPoint(latitude, longitude);
    }

	@Override
	public String toString() {
		return "ESCityDocument [id=" + id + ", city=" + city + ", isActivated=" + isActivated + ", explanation="
				+ explanation + ", state=" + state + ", country=" + country + ", geoPoint=" + geoPoint + ", latitude="
				+ latitude + ", longitude=" + longitude + "]";
	}

}
