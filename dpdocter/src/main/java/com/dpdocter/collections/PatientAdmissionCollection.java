package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "patient_admission_cl")
public class PatientAdmissionCollection {

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

    @Field
    private String hospitalId;

    @Field
    private String patientId;

    @Field
    private String doctorId;

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

    public String getHospitalId() {
	return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
	this.hospitalId = hospitalId;
    }

    public String getPatientId() {
	return patientId;
    }

    public void setPatientId(String patientId) {
	this.patientId = patientId;
    }

    public String getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(String doctorId) {
	this.doctorId = doctorId;
    }

    @Override
    public String toString() {
	return "PatientAdmissionCollection [id=" + id + ", userId=" + userId + ", locationId=" + locationId + ", dateOfVisit=" + dateOfVisit
		+ ", pastHistoryId=" + pastHistoryId + ", medicalHistoryId=" + medicalHistoryId + ", patientNumber=" + patientNumber + ", referredBy="
		+ referredBy + ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", doctorId=" + doctorId + "]";
    }

}
