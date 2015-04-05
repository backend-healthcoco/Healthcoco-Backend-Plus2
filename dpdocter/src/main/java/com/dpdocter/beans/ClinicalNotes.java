package com.dpdocter.beans;

import java.util.List;

public class ClinicalNotes {

	private String id;

	private Complaint complaints;

	private Observation observation;

	private Investigation investigation;

	private Diagnosis diagnoses;
	
	private Long createdDate;

	private List<Diagram> diagrams;

	private Notes notes;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Complaint getComplaints() {
		return complaints;
	}

	public void setComplaints(Complaint complaints) {
		this.complaints = complaints;
	}

	public Observation getObservation() {
		return observation;
	}

	public void setObservation(Observation observation) {
		this.observation = observation;
	}

	public Investigation getInvestigation() {
		return investigation;
	}

	public void setInvestigation(Investigation investigation) {
		this.investigation = investigation;
	}

	public Diagnosis getDiagnoses() {
		return diagnoses;
	}

	public void setDiagnoses(Diagnosis diagnoses) {
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

	public Notes getNotes() {
		return notes;
	}

	public void setNotes(Notes notes) {
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

	@Override
	public String toString() {
		return "ClinicalNotes [id=" + id + ", complaints=" + complaints
				+ ", observation=" + observation + ", investigation="
				+ investigation + ", diagnoses=" + diagnoses + ", createdDate="
				+ createdDate + ", diagrams=" + diagrams + ", notes=" + notes
				+ ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + "]";
	}

	
	
	
}
