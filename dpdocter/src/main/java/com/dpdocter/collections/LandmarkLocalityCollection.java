package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "landmark_locality_cl")
public class LandmarkLocalityCollection {

    @Id
    private ObjectId id;

    @Field
    private ObjectId cityId;

    @Field
    private String locality;

    @Field
    private String landmark;

    @Field
    private String explanation;

    @Field
    private Double latitude;

    @Field
    private Double longitude;

    public ObjectId getId() {
	return id;
    }

    public void setId(ObjectId id) {
	this.id = id;
    }

    public ObjectId getCityId() {
	return cityId;
    }

    public void setCityId(ObjectId cityId) {
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

    public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
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
	return "LandmarkLocalityCollection [id=" + id + ", cityId=" + cityId + ", locality=" + locality + ", landmark=" + landmark + ", explanation="
		+ explanation + ", latitude=" + latitude + ", longitude=" + longitude + "]";
    }
}
