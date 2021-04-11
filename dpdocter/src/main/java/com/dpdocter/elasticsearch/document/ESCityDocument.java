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

<<<<<<< HEAD
    @Field(type = FieldType.Text)
=======
    @Field(type = FieldType.Text, fielddata = true)
>>>>>>> 26d38db... HAPPY-6129
    private String city;

    @Field(type = FieldType.Boolean)
    private Boolean isActivated = false;

    @Field(type = FieldType.Text)
    private String explanation;

    @Field(type = FieldType.Text)
    private String state;

    @Field(type = FieldType.Text)
    private String country;

    @GeoPointField
    private GeoPoint geoPoint;

    @Field(type = FieldType.Double)
    private Double latitude;

    @Field(type = FieldType.Double)
    private Double longitude;

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

	@Override
	public String toString() {
		return "ESCityDocument [id=" + id + ", city=" + city + ", isActivated=" + isActivated + ", explanation="
				+ explanation + ", state=" + state + ", country=" + country + ", geoPoint=" + geoPoint + ", latitude="
				+ latitude + ", longitude=" + longitude + "]";
	}

}
