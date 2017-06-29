package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class BabyNote extends GenericCollection {

	private String id;

	private String BabyNotes;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private Boolean discarded = false;

	private String speciality;

	public String getId() {
		return id;
	}

	public String getBabyNotes() {
		return BabyNotes;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setBabyNotes(String babyNotes) {
		BabyNotes = babyNotes;
	}

	public String getLocationId() {
		return locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

}
