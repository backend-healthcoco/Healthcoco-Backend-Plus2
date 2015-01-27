package com.dpdocter.collections;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="clinical_notes_cl")
public class ClinicalNotesCollection {

	@Id
	private String id;
	
	@Field
	private String complaints;
	
	@Field
	private String observation;
	
	@Field
	private String investigation;
	
	@Field
	private String diagnoses;
	
	@Field
	private List<String> diagramUrls;
	
	@Field
	private String comments;
	
	@Field
	private String doctorId;
	
	@Field
	private String locationId;
	
	@Field
	private String hospitalId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
		return "ClinicalNotesCollection [id=" + id + ", complaints="
				+ complaints + ", observation=" + observation
				+ ", investigation=" + investigation + ", diagnoses="
				+ diagnoses + ", diagramUrls=" + diagramUrls + ", comments="
				+ comments + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + "]";
	}
	
	
	
	
	
}
