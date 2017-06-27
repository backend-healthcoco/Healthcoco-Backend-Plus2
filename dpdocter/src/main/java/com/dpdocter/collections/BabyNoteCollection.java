package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "babyNote_cl")
public class BabyNoteCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private String babyNotes;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private boolean inHistory = false;
	@Field
	private Boolean discarded = false;
	@Field
	private String speciality;
	public ObjectId getId() {
		return id;
	}
	public String getBabyNotes() {
		return babyNotes;
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
	public boolean isInHistory() {
		return inHistory;
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
	public void setBabyNotes(String babyNotes) {
		this.babyNotes = babyNotes;
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
	public void setInHistory(boolean inHistory) {
		this.inHistory = inHistory;
	}
	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}
	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}
	

}
