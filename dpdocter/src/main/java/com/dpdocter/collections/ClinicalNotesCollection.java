package com.dpdocter.collections;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "clinical_notes_cl")
public class ClinicalNotesCollection extends GenericCollection {

    @Id
    private String id;

    @Field
    private List<String> notes;

    @Field
    private List<String> observations;

    @Field
    private List<String> investigations;

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
    private Boolean discarded = false;

    @Field
    private boolean inHistory = false;

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

    public List<String> getObservations() {
	return observations;
    }

    public void setObservations(List<String> observations) {
	this.observations = observations;
    }

    public List<String> getInvestigations() {
	return investigations;
    }

    public void setInvestigations(List<String> investigations) {
	this.investigations = investigations;
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

    public boolean isInHistory() {
	return inHistory;
    }

    public void setInHistory(boolean inHistory) {
	this.inHistory = inHistory;
    }

    @Override
	public String toString() {
		return "ClinicalNotesCollection [id=" + id + ", notes=" + notes + ", observations=" + observations
				+ ", investigations=" + investigations + ", diagnoses=" + diagnoses + ", complaints=" + complaints
				+ ", diagrams=" + diagrams + ", diagramsPaths=" + diagramsPaths + ", comments=" + comments
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", discarded=" + discarded + ", inHistory=" + inHistory + "]";
	}

}
