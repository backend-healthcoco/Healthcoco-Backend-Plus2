package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public class NursingCareExamCollection extends GenericCollection{

	@Id
	private ObjectId id;
	@Field
	private String nursingCare;
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
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getNursingCare() {
		return nursingCare;
	}
	public void setNursingCare(String nursingCare) {
		this.nursingCare = nursingCare;
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
	public Boolean getDiscarded() {
		return discarded;
	}
	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}
	public String getSpeciality() {
		return speciality;
	}
	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}
	
	
}
