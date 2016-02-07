package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.FeedbackType;

public class Feedback extends GenericCollection {

    private String id;

    private FeedbackType type;

    private String doctorName;

    private String landmarkLocality;

    private String city;

    private String description;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public FeedbackType getType() {
	return type;
    }

    public void setType(FeedbackType type) {
	this.type = type;
    }

    public String getDoctorName() {
	return doctorName;
    }

    public void setDoctorName(String doctorName) {
	this.doctorName = doctorName;
    }

    public String getLandmarkLocality() {
	return landmarkLocality;
    }

    public void setLandmarkLocality(String landmarkLocality) {
	this.landmarkLocality = landmarkLocality;
    }

    public String getCity() {
	return city;
    }

    public void setCity(String city) {
	this.city = city;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    @Override
    public String toString() {
	return "Feedback [id=" + id + ", type=" + type + ", doctorName=" + doctorName + ", landmarkLocality=" + landmarkLocality + ", city=" + city
		+ ", description=" + description + "]";
    }
}
