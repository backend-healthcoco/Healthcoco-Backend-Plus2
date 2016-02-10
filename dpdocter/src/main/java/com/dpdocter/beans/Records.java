package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class Records extends GenericCollection {
    private String id;

    private String uniqueId;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private String patientId;

    private String recordsUrl;

    private String recordsLable;

    private String recordsType;

    private String description;

    private boolean inHistory = false;

    private Boolean discarded = false;

    private String uploadedByLocation;

    private String visitId;

    private String prescriptionId;

    private String prescribedByDoctorId;

    private String prescribedByLocationId;

    private String prescribedByHospitalId;

    private String testId;;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
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

    public String getRecordsType() {
	return recordsType;
    }

    public void setRecordsType(String recordsType) {
	this.recordsType = recordsType;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public boolean isInHistory() {
	return inHistory;
    }

    public void setInHistory(boolean inHistory) {
	this.inHistory = inHistory;
    }

    public Boolean getDiscarded() {
	return discarded;
    }

    public void setDiscarded(Boolean discarded) {
	this.discarded = discarded;
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

    public String getVisitId() {
	return visitId;
    }

    public void setVisitId(String visitId) {
	this.visitId = visitId;
    }

    public String getPatientId() {
	return patientId;
    }

    public void setPatientId(String patientId) {
	this.patientId = patientId;
    }

    public String getUploadedByLocation() {
	return uploadedByLocation;
    }

    public void setUploadedByLocation(String uploadedByLocation) {
	this.uploadedByLocation = uploadedByLocation;
    }

    public String getUniqueId() {
	return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
	this.uniqueId = uniqueId;
    }

    public String getPrescriptionId() {
	return prescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
	this.prescriptionId = prescriptionId;
    }

    public String getPrescribedByDoctorId() {
	return prescribedByDoctorId;
    }

    public void setPrescribedByDoctorId(String prescribedByDoctorId) {
	this.prescribedByDoctorId = prescribedByDoctorId;
    }

    public String getPrescribedByLocationId() {
	return prescribedByLocationId;
    }

    public void setPrescribedByLocationId(String prescribedByLocationId) {
	this.prescribedByLocationId = prescribedByLocationId;
    }

    public String getPrescribedByHospitalId() {
	return prescribedByHospitalId;
    }

    public void setPrescribedByHospitalId(String prescribedByHospitalId) {
	this.prescribedByHospitalId = prescribedByHospitalId;
    }

    public String getTestId() {
	return testId;
    }

    public void setTestId(String testId) {
	this.testId = testId;
    }

    @Override
    public String toString() {
	return "Records [id=" + id + ", uniqueId=" + uniqueId + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
		+ ", patientId=" + patientId + ", recordsUrl=" + recordsUrl + ", recordsLable=" + recordsLable + ", recordsType=" + recordsType
		+ ", description=" + description + ", inHistory=" + inHistory + ", discarded=" + discarded + ", uploadedByLocation=" + uploadedByLocation
		+ ", visitId=" + visitId + ", prescriptionId=" + prescriptionId + ", prescribedByDoctorId=" + prescribedByDoctorId + ", prescribedByLocationId="
		+ prescribedByLocationId + ", prescribedByHospitalId=" + prescribedByHospitalId + ", testId=" + testId + "]";
    }
}
