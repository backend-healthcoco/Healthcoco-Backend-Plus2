package com.dpdocter.collections;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "notes_cl")
public class NotesCollection {

	@Field
	private String id;

	@Field
	private String notes;

	@Field
	private String doctorId;

	@Field
	private String locationId;

	@Field
	private String hospitalId;

	@Field
	private boolean isDeleted;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	@Override
	public String toString() {
		return "NotesCollection [id=" + id + ", notes=" + notes + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", isDeleted=" + isDeleted + "]";
	}

}
