package com.dpdocter.request;

import java.util.List;

public class ClinicalNotesAddRequest {
	private String id;
	
	private String patientId;

	private String complaints;

	private String observation;

	private String investigation;

	private String diagnoses;

	private List<String> diagrams;

	private String notes;

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

	

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getObservation() {
		return observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
	}

	public String getInvestigation() {
		return investigation;
	}

	public void setInvestigation(String investigation) {
		this.investigation = investigation;
	}

	public String getDiagnoses() {
		return diagnoses;
	}

	public void setDiagnoses(String diagnoses) {
		this.diagnoses = diagnoses;
	}


	

	public String getComplaints() {
		return complaints;
	}

	public void setComplaints(String complaints) {
		this.complaints = complaints;
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



	public List<String> getDiagrams() {
		return diagrams;
	}

	public void setDiagrams(List<String> diagrams) {
		this.diagrams = diagrams;
	}

	@Override
	public String toString() {
		return "ClinicalNotesAddRequest [id=" + id + ", patientId=" + patientId
				+ ", complaints=" + complaints + ", observation=" + observation
				+ ", investigation=" + investigation + ", diagnoses="
				+ diagnoses + ", diagrams=" + diagrams + ", notes=" + notes
				+ ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", createdDate=" + createdDate
				+ "]";
	}

	

	
	
	
}
