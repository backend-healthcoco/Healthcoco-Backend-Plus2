package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class Records extends GenericCollection {
    private String id;

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

    private String doctorName;

    private String clinicName;

    private String visitId;

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

    public String getDoctorName() {
	return doctorName;
    }

    public void setDoctorName(String doctorName) {
	this.doctorName = doctorName;
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

    public String getClinicName() {
	return clinicName;
    }

    public void setClinicName(String clinicName) {
	this.clinicName = clinicName;

    }

    @Override
    public String toString() {
	return "Records [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", patientId=" + patientId
		+ ", recordsUrl=" + recordsUrl + ", recordsLable=" + recordsLable + ", recordsType=" + recordsType + ", description=" + description
		+ ", inHistory=" + inHistory + ", discarded=" + discarded + ", doctorName=" + doctorName + ", clinicName=" + clinicName + ", visitId="
		+ visitId + "]";
    }
}
