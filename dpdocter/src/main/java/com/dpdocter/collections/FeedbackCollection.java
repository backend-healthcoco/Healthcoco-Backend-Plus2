package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.FeedbackType;

@Document(collection = "feedback_cl")
public class FeedbackCollection extends GenericCollection{

	@Id
	private String id;
	
	@Field
	private FeedbackType type;
	
	@Field
	private String doctorName;
	
	@Field
	private String landmarkLocality;
	
	@Field
	private String city;
	
	@Field
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
		return "FeedbackCollection [id=" + id + ", type=" + type + ", doctorName=" + doctorName + ", landmarkLocality="
				+ landmarkLocality + ", city=" + city + ", description=" + description + "]";
	}
}
