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
	private String recordsLable;
	@Field
	private String description;
	@Field
	private String patientId;
	@Field
	private String doctorId;
	@Field
	private Long createdDate;
	
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
	public String getrecordsLable() {
		return recordsLable;
	}
	public void setrecordsLable(String recordsLable) {
		this.recordsLable = recordsLable;
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
	
	public String getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}
	@Override
	public String toString() {
		return "recordssCollection [id=" + id + ", recordsUrl=" + recordsUrl
				+ ", recordsLable=" + recordsLable + ", description="
				+ description + ", patientId=" + patientId + ", doctorId="
				+ doctorId + ", createdDate=" + createdDate + "]";
	}
	
	
}
