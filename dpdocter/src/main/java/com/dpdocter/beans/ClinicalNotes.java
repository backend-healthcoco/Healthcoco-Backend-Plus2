package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class ClinicalNotes extends GenericCollection {

    private String id;

    private List<Complaint> complaints;

    private List<Observation> observations;

    private List<Investigation> investigations;

    private List<Diagnoses> diagnoses;

    private List<Diagram> diagrams;

    private List<Notes> notes;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private String doctorName;

    private boolean inHistory = false;

    private Boolean discarded = false;

    private String visitId;

    private String patientId;

    private VitalSigns vitalSigns;

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

    public Boolean getDiscarded() {
	return discarded;
    }

    public void setDiscarded(Boolean discarded) {
	this.discarded = discarded;
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

    public VitalSigns getVitalSigns() {
	return vitalSigns;
    }

    public void setVitalSigns(VitalSigns vitalSigns) {
	this.vitalSigns = vitalSigns;
    }

	@Override
	public String toString() {
		return "ClinicalNotes [id=" + id + ", complaints=" + complaints + ", observations=" + observations
				+ ", investigations=" + investigations + ", diagnoses=" + diagnoses + ", diagrams=" + diagrams
				+ ", notes=" + notes + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", doctorName=" + doctorName + ", inHistory=" + inHistory + ", discarded=" + discarded
				+ ", visitId=" + visitId + ", patientId=" + patientId + ", vitalSigns=" + vitalSigns + "]";
	}
}
