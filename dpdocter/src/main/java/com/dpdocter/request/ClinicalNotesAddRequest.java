package com.dpdocter.request;

import java.util.List;

public class ClinicalNotesAddRequest {
	private String id;

	private String patientId;

	private List<String> complaints;

	private List<String> observation;

	private List<String> investigation;

	private List<String> diagnoses;

	private List<String> diagrams;

	private List<String> notes;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private Long createdDate;

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

	public List<String> getComplaints() {
		return complaints;
	}

	public void setComplaints(List<String> complaints) {
		this.complaints = complaints;
	}

	public List<String> getObservation() {
		return observation;
	}

	public void setObservation(List<String> observation) {
		this.observation = observation;
	}

	public List<String> getInvestigation() {
		return investigation;
	}

	public void setInvestigation(List<String> investigation) {
		this.investigation = investigation;
	}

	public List<String> getDiagnoses() {
		return diagnoses;
	}

	public void setDiagnoses(List<String> diagnoses) {
		this.diagnoses = diagnoses;
	}

	public List<String> getDiagrams() {
		return diagrams;
	}

	public void setDiagrams(List<String> diagrams) {
		this.diagrams = diagrams;
	}

	public List<String> getNotes() {
		return notes;
	}

	public void setNotes(List<String> notes) {
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

	public Long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Long createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public String toString() {
		return "ClinicalNotesAddRequest [id=" + id + ", patientId=" + patientId + ", complaints=" + complaints + ", observation=" + observation
				+ ", investigation=" + investigation + ", diagnoses=" + diagnoses + ", diagrams=" + diagrams + ", notes=" + notes + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", createdDate=" + createdDate + "]";
	}

}
