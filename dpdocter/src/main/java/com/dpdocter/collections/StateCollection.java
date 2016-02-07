package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "state_cl")
public class StateCollection {

    @Id
    private String id;

    @Indexed(unique = true)
    private String state;

    @Field
    private String description;

    @Field
    private Boolean isActivated = true;

    @Field
    private String countryId;

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

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public Boolean getIsActivated() {
	return isActivated;
    }

    public void setIsActivated(Boolean isActivated) {
	this.isActivated = isActivated;
    }

    public String getCountryId() {
	return countryId;
    }

    public void setCountryId(String countryId) {
	this.countryId = countryId;
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
	return "StateCollection [id=" + id + ", state=" + state + ", description=" + description + ", isActivated=" + isActivated + ", countryId=" + countryId
		+ ", latitude=" + latitude + ", longitude=" + longitude + "]";
    }
}
