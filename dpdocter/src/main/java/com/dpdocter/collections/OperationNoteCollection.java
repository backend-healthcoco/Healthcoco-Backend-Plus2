package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "operationNote_cl")
public class OperationNoteCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String operationNotes;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private Boolean discarded = false;
	@Field
	private String speciality;

	public ObjectId getId() {
		return id;
	}

	public String getOperationNotes() {
		return operationNotes;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public void setOperationNotes(String operationNotes) {
		this.operationNotes = operationNotes;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

}
