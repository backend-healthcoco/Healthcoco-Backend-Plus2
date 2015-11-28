package com.dpdocter.beans;

import java.util.List;

import com.mongodb.DBObject;

public class ClinicalNotesJasperDetails {

    private String complaints;

    private String observations;

    private String investigations;

    private String diagnosis;

    private String notes;

    private List<DBObject> diagrams;

    private String vitalSigns;
    
    public String getComplaints() {
	return complaints;
    }

    public void setComplaints(String complaints) {
	this.complaints = complaints;
    }

    public String getObservations() {
	return observations;
    }

    public void setObservations(String observations) {
	this.observations = observations;
    }

    public String getInvestigations() {
	return investigations;
    }

    public void setInvestigations(String investigations) {
	this.investigations = investigations;
    }

    public String getDiagnosis() {
	return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
	this.diagnosis = diagnosis;
    }

    public List<DBObject> getDiagrams() {
	return diagrams;
    }

    public void setDiagrams(List<DBObject> list) {
	this.diagrams = list;
    }

    public String getNotes() {
	return notes;
    }

    public void setNotes(String notes) {
	this.notes = notes;
    }

	public String getVitalSigns() {
		return vitalSigns;
	}

	public void setVitalSigns(String vitalSigns) {
		this.vitalSigns = vitalSigns;
	}

	@Override
	public String toString() {
		return "ClinicalNotesJasperDetails [complaints=" + complaints + ", observations=" + observations
				+ ", investigations=" + investigations + ", diagnosis=" + diagnosis + ", notes=" + notes + ", diagrams="
				+ diagrams + ", vitalSigns=" + vitalSigns + "]";
	}
}
