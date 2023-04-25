package com.dpdocter.elasticsearch.document;

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

	@Field(type = FieldType.Text)
	private String cityId;

	@Field(type = FieldType.Text)
	private String locality;

	@Field(type = FieldType.Text)
	private String landmark;

	@Field(type = FieldType.Text)
	private String explanation;

	@GeoPointField
	private GeoPoint geoPoint;

	@Field(type = FieldType.Double)
	private Double latitude;

	@Field(type = FieldType.Double)
	private Double longitude;

	@Field(type = FieldType.Text)
	private String postalCode;

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

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	@Override
	public String toString() {
		return "ESLandmarkLocalityDocument [id=" + id + ", cityId=" + cityId + ", locality=" + locality + ", landmark="
				+ landmark + ", explanation=" + explanation + ", geoPoint=" + geoPoint + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", postalCode=" + postalCode + "]";
	}
}
