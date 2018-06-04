package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class DentalImagingLabDoctorAssociationCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId dentalImagingLocationId;
	@Field
	private ObjectId dentalImagingHospitalId;
	@Field
	private Boolean discarded = false;

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

	public ObjectId getDentalImagingLocationId() {
		return dentalImagingLocationId;
	}

	public void setDentalImagingLocationId(ObjectId dentalImagingLocationId) {
		this.dentalImagingLocationId = dentalImagingLocationId;
	}

	public ObjectId getDentalImagingHospitalId() {
		return dentalImagingHospitalId;
	}

	public void setDentalImagingHospitalId(ObjectId dentalImagingHospitalId) {
		this.dentalImagingHospitalId = dentalImagingHospitalId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

}
