package com.dpdocter.elasticsearch.document;

import java.util.Arrays;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Document(indexName = "landmarkslocalities_in", type = "landmarkslocalities")
public class ESLandmarkLocalityDocument {

    @Id
    private String id;

    @Field(type = FieldType.String)
    private String cityId;

    @Field(type = FieldType.String)
    private String locality;

    @Field(type = FieldType.String)
    private String landmark;

    @Field(type = FieldType.String)
    private String explanation;

    @Field(type = FieldType.Object)
    @GeoPointField
    private GeoPoint geoPoint;

    @GeoPointField
	private double[] locationAsArray;
    
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

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public double[] getLocationAsArray() {
		return locationAsArray;
	}

	public void setLocationAsArray(double[] locationAsArray) {
		this.locationAsArray = locationAsArray;
	}

	@Override
	public String toString() {
		return "ESLandmarkLocalityDocument [id=" + id + ", cityId=" + cityId + ", locality=" + locality + ", landmark="
				+ landmark + ", explanation=" + explanation + ", geoPoint=" + geoPoint + ", locationAsArray="
				+ Arrays.toString(locationAsArray) + ", latitude=" + latitude + ", longitude=" + longitude + "]";
	}
}
