package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

public class MyVideoCollection extends GenericCollection {

	@Field
	private ObjectId id;

	@Field
	private ObjectId doctorId;

	@Field
	private ObjectId hospitalId;

	@Field
	private ObjectId locationId;

	@Field
	private String name;

	@Field
	private Boolean discarded = false;

	@Field
	private String videoUrl;

	@Field
	private String description;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "MyVideoCollection [id=" + id + ", doctorId=" + doctorId + ", hospitalId=" + hospitalId + ", locationId="
				+ locationId + ", name=" + name + ", discarded=" + discarded + ", videoUrl=" + videoUrl
				+ ", description=" + description + "]";
	}

}
