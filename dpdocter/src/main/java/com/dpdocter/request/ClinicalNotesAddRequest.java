package com.dpdocter.request;

import java.util.List;

public class ClinicalNotesAddRequest {
	private String id;
	
	private String patientId;

	private String complaints;

	private String observation;

	private String investigation;

	private String diagnoses;

	private List<String> diagramUrls;

	private String comments;

	private String doctorId;

	private String locationId;

	private String hospitalId;
	
	

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

	public String getComplaints() {
		return complaints;
	}

	public void setComplaints(String complaints) {
		this.complaints = complaints;
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

	public List<String> getDiagramUrls() {
		return diagramUrls;
	}

	public void setDiagramUrls(List<String> diagramUrls) {
		this.diagramUrls = diagramUrls;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
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
		return "ClinicalNotesAddRequest [id=" + id + ", patientId=" + patientId
				+ ", complaints=" + complaints + ", observation=" + observation
				+ ", investigation=" + investigation + ", diagnoses="
				+ diagnoses + ", diagramUrls=" + diagramUrls + ", comments="
				+ comments + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + "]";
	}

	
	
	
	
}
