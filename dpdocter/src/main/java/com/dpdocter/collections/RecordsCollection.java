package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="recordss_cl")
public class RecordsCollection {
	
	@Id
	private String id;
	@Field
	private String recordsUrl;
	@Field
	private String recordsPath;
	@Field
	private String recordsLable;
	@Field
	private String description;
	@Field
	private String patientId;
	@Field
	private String doctorId;
	@Field
	private Long createdDate;
	@Field
	private String locationId;
	@Field
	private String hospitalId;
	@Field
	private boolean isDeleted = false;
	
	
	public String getRecordsPath() {
		return recordsPath;
	}
	public void setRecordsPath(String recordsPath) {
		this.recordsPath = recordsPath;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getrecordsUrl() {
		return recordsUrl;
	}
	public void setrecordsUrl(String recordsUrl) {
		this.recordsUrl = recordsUrl;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	
	public Long getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Long createdDate) {
		this.createdDate = createdDate;
	}
	
	public boolean isDeleted() {
		return isDeleted;
	}
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public String getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}
	
	public String getRecordsUrl() {
		return recordsUrl;
	}
	public void setRecordsUrl(String recordsUrl) {
		this.recordsUrl = recordsUrl;
	}

	public String getRecordsLable() {
		return recordsLable;
	}
	public void setRecordsLable(String recordsLable) {
		this.recordsLable = recordsLable;
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
	@Override
	public String toString() {
		return "RecordsCollection [id=" + id + ", recordsUrl=" + recordsUrl
				+ ", recordsPath=" + recordsPath + ", recordsLable="
				+ recordsLable + ", description=" + description
				+ ", patientId=" + patientId + ", doctorId=" + doctorId
				+ ", createdDate=" + createdDate + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", isDeleted=" + isDeleted
				+ "]";
	}
	
}
