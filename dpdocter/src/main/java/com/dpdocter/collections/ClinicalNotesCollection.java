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
	private List<String> notes;
	
	@Field
	private List<String> observation;
	
	@Field
	private List<String> investigation;
	
	@Field
	private List<String> diagnoses;
	
	@Field
	private List<String> complaints;
	
	@Field
	private List<String> diagrams;
	
	@Field
	private List<String> diagramsPaths;
	
	@Field
	private List<String> comments;
	
	@Field
	private String doctorId;
	
	@Field
	private String locationId;
	
	@Field
	private String hospitalId;
	
	@Field
	private Long createdDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getNotes() {
		return notes;
	}

	public void setNotes(List<String> notes) {
		this.notes = notes;
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

	public List<String> getComplaints() {
		return complaints;
	}

	public void setComplaints(List<String> complaints) {
		this.complaints = complaints;
	}

	public List<String> getDiagrams() {
		return diagrams;
	}

	public void setDiagrams(List<String> diagrams) {
		this.diagrams = diagrams;
	}

	public List<String> getDiagramsPaths() {
		return diagramsPaths;
	}

	public void setDiagramsPaths(List<String> diagramsPaths) {
		this.diagramsPaths = diagramsPaths;
	}

	public List<String> getComments() {
		return comments;
	}

	public void setComments(List<String> comments) {
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

	public Long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Long createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public String toString() {
		return "ClinicalNotesCollection [id=" + id + ", notes=" + notes
				+ ", observation=" + observation + ", investigation="
				+ investigation + ", diagnoses=" + diagnoses + ", complaints="
				+ complaints + ", diagrams=" + diagrams + ", diagramsPaths="
				+ diagramsPaths + ", comments=" + comments + ", doctorId="
				+ doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", createdDate=" + createdDate + "]";
	}

}
