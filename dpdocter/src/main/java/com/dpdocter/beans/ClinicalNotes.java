package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class ClinicalNotes extends GenericCollection {

    private String id;

    private List<Complaint> complaints;

    private List<Observation> observations;

    private List<Investigation> investigations;

    private List<Diagnoses> diagnoses;

    private Long createdDate;

    private List<Diagram> diagrams;

    private List<Notes> notes;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private boolean inHistory = false;

    private Boolean isDeleted = false;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public List<Complaint> getComplaints() {
	return complaints;
    }

    public void setComplaints(List<Complaint> complaints) {
	this.complaints = complaints;
    }

    public List<Observation> getObservations() {
	return observations;
    }

    public void setObservations(List<Observation> observations) {
	this.observations = observations;
    }

    public List<Investigation> getInvestigations() {
	return investigations;
    }

    public void setInvestigations(List<Investigation> investigations) {
	this.investigations = investigations;
    }

    public List<Diagnoses> getDiagnoses() {
	return diagnoses;
    }

    public void setDiagnoses(List<Diagnoses> diagnoses) {
	this.diagnoses = diagnoses;
    }

    public Long getCreatedDate() {
	return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
	this.createdDate = createdDate;
    }

    public List<Diagram> getDiagrams() {
	return diagrams;
    }

    public void setDiagrams(List<Diagram> diagrams) {
	this.diagrams = diagrams;
    }

    public List<Notes> getNotes() {
	return notes;
    }

    public void setNotes(List<Notes> notes) {
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

    public boolean isInHistory() {
	return inHistory;
    }

    public void setInHistory(boolean inHistory) {
	this.inHistory = inHistory;
    }

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	@Override
	public String toString() {
		return "ClinicalNotes [id=" + id + ", complaints=" + complaints + ", observations=" + observations
				+ ", investigations=" + investigations + ", diagnoses=" + diagnoses + ", createdDate=" + createdDate
				+ ", diagrams=" + diagrams + ", notes=" + notes + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", inHistory=" + inHistory + ", isDeleted=" + isDeleted
				+ "]";
	}
}
