package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "custom_work_cl")
public class DentalWorkCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String workName;
	@Field
	private Boolean isShadeRequired = false;
	@Field
	private Boolean discarded = false;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getWorkName() {
		return workName;
	}

	public void setWorkName(String workName) {
		this.workName = workName;
	}

	public Boolean getIsShadeRequired() {
		return isShadeRequired;
	}

	public void setIsShadeRequired(Boolean isShadeRequired) {
		this.isShadeRequired = isShadeRequired;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	@Override
	public String toString() {
		return "CustomWorkCollection [id=" + id + ", workName=" + workName + ", isShadeRequired=" + isShadeRequired
				+ ", discarded=" + discarded + "]";
	}

}
