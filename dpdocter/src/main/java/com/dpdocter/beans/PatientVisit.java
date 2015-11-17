package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.VisitedFor;

public class PatientVisit {
    private String id;

    private String patientId;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private Date visitedTime;

    private List<VisitedFor> visitedFor;

    private List<String> prescriptionId;

    private List<String> clinicalNotesId;

    private List<String> recordId;
    
    private Boolean discarded = false;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
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

    public Date getVisitedTime() {
	return visitedTime;
    }

    public void setVisitedTime(Date visitedTime) {
	this.visitedTime = visitedTime;
    }

    public List<VisitedFor> getVisitedFor() {
	return visitedFor;
    }

    public void setVisitedFor(List<VisitedFor> visitedFor) {
	this.visitedFor = visitedFor;
    }

    public List<String> getPrescriptionId() {
	return prescriptionId;
    }

    public void setPrescriptionId(List<String> prescriptionId) {
	this.prescriptionId = prescriptionId;
    }

    public List<String> getClinicalNotesId() {
	return clinicalNotesId;
    }

    public void setClinicalNotesId(List<String> clinicalNotesId) {
	this.clinicalNotesId = clinicalNotesId;
    }

    public List<String> getRecordId() {
	return recordId;
    }

    public void setRecordId(List<String> recordId) {
	this.recordId = recordId;
    }

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "PatientVisit [id=" + id + ", patientId=" + patientId + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", visitedTime=" + visitedTime + ", visitedFor="
				+ visitedFor + ", prescriptionId=" + prescriptionId + ", clinicalNotesId=" + clinicalNotesId
				+ ", recordId=" + recordId + ", discarded=" + discarded + "]";
	}
}
