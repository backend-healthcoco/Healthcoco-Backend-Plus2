package com.dpdocter.elasticsearch.document;

import java.util.Date;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
@Document(indexName = "labournote_in", type = "labournote")
public class EsLabourNoteDocument {
	@Field(type = FieldType.Text)
	private String id;

	@Field(type = FieldType.Text)
	private String labourNotes;

	@Field(type = FieldType.Text)
	private String doctorId;

	@Field(type = FieldType.Text)
	private String locationId;

	@Field(type = FieldType.Text)
	private String hospitalId;

	@Field(type = FieldType.Boolean)
	private Boolean discarded = false;

	@Field(type = FieldType.Date)
	private Date updatedTime = new Date();

	@Field(type = FieldType.Text)
	private String speciality;

	public String getId() {
		return id;
	}

	public String getLabourNotes() {
		return labourNotes;
	}

	public String getDoctorId() {
		return doctorId;
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

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLabourNotes(String labourNotes) {
		this.labourNotes = labourNotes;
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

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}


}
