package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.ClinicalNotesComplaint;
import com.dpdocter.beans.ClinicalNotesDiagnosis;
import com.dpdocter.beans.ClinicalNotesInvestigation;
import com.dpdocter.beans.ClinicalNotesNote;
import com.dpdocter.beans.ClinicalNotesObservation;

public class ClinicalNotesAddRequest {
    private String id;

    private String patientId;

    private List<ClinicalNotesComplaint> complaints;

    private List<ClinicalNotesObservation> observations;

    private List<ClinicalNotesInvestigation> investigations;

    private List<ClinicalNotesDiagnosis> diagnoses;

    private List<ClinicalNotesNote> notes;

    private List<String> diagrams;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private String createdBy;

    private String visitId;

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

    public List<ClinicalNotesComplaint> getComplaints() {
	return complaints;
    }

    public void setComplaints(List<ClinicalNotesComplaint> complaints) {
	this.complaints = complaints;
    }

    public List<ClinicalNotesObservation> getObservations() {
	return observations;
    }

    public void setObservations(List<ClinicalNotesObservation> observations) {
	this.observations = observations;
    }

    public List<ClinicalNotesInvestigation> getInvestigations() {
	return investigations;
    }

    public void setInvestigations(List<ClinicalNotesInvestigation> investigations) {
	this.investigations = investigations;
    }

    public List<ClinicalNotesDiagnosis> getDiagnoses() {
	return diagnoses;
    }

    public void setDiagnoses(List<ClinicalNotesDiagnosis> diagnoses) {
	this.diagnoses = diagnoses;
    }

    public List<ClinicalNotesNote> getNotes() {
	return notes;
    }

    public void setNotes(List<ClinicalNotesNote> notes) {
	this.notes = notes;
    }

    public List<String> getDiagrams() {
	return diagrams;
    }

    public void setDiagrams(List<String> diagrams) {
	this.diagrams = diagrams;
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

    public String getCreatedBy() {
	return createdBy;
    }

    public void setCreatedBy(String createdBy) {
	this.createdBy = createdBy;
    }

    public String getVisitId() {
	return visitId;
    }

    public void setVisitId(String visitId) {
	this.visitId = visitId;
    }

    @Override
    public String toString() {
	return "ClinicalNotesAddRequest [id=" + id + ", patientId=" + patientId + ", complaints=" + complaints + ", observations=" + observations
		+ ", investigations=" + investigations + ", diagnoses=" + diagnoses + ", notes=" + notes + ", diagrams=" + diagrams + ", doctorId=" + doctorId
		+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", createdBy=" + createdBy + ", visitId=" + visitId + "]";
    }

}
