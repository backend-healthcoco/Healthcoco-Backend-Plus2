package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="patient_cl")
public class PatientCollection {

	@Id
	private String id;
	
	@Field
	private String userId;

	@Field
	private String locationId;
	
	@Field
	private Long dateOfVisit;
	
	@Field
	private String pastHistoryId;
	
	@Field
	private String medicalHistoryId;
	
	@Field
	private String patientNumber;
	
	@Field
	private String referredBy;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public Long getDateOfVisit() {
		return dateOfVisit;
	}

	public void setDateOfVisit(Long dateOfVisit) {
		this.dateOfVisit = dateOfVisit;
	}

	public String getPastHistoryId() {
		return pastHistoryId;
	}

	public void setPastHistoryId(String pastHistoryId) {
		this.pastHistoryId = pastHistoryId;
	}

	public String getMedicalHistoryId() {
		return medicalHistoryId;
	}

	public void setMedicalHistoryId(String medicalHistoryId) {
		this.medicalHistoryId = medicalHistoryId;
	}

	public String getPatientNumber() {
		return patientNumber;
	}

	public void setPatientNumber(String patientNumber) {
		this.patientNumber = patientNumber;
	}
	

	public String getReferredBy() {
		return referredBy;
	}

	public void setReferredBy(String referredBy) {
		this.referredBy = referredBy;
	}

	@Override
	public String toString() {
		return "PatientCollection [id=" + id + ", userId=" + userId
				+ ", locationId=" + locationId + ", dateOfVisit=" + dateOfVisit
				+ ", pastHistoryId=" + pastHistoryId + ", medicalHistoryId="
				+ medicalHistoryId + ", patientNumber=" + patientNumber
				+ ", referredBy=" + referredBy + "]";
	}

}
