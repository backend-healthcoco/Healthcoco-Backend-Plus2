package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mongodb.DBObject;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ClinicalNotesJasperDetails {

    private String complaints;

    private String observations;

    private String investigations;

    private String diagnosis;

    private String notes;

    private List<DBObject> diagrams;

    private String vitalSigns;

    private String presentComplaint;
    
	private String provisionalDiagnosis;

	private String generalExam;

	private String systemExam;

	private String presentComplaintHistory;

	private String menstrualHistory;
	
	private String obstetricHistory;

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

	public String getPresentComplaint() {
		return presentComplaint;
	}

	public void setPresentComplaint(String presentComplaint) {
		this.presentComplaint = presentComplaint;
	}

	public String getProvisionalDiagnosis() {
		return provisionalDiagnosis;
	}

	public void setProvisionalDiagnosis(String provisionalDiagnosis) {
		this.provisionalDiagnosis = provisionalDiagnosis;
	}

	public String getGeneralExam() {
		return generalExam;
	}

	public void setGeneralExam(String generalExam) {
		this.generalExam = generalExam;
	}

	public String getSystemExam() {
		return systemExam;
	}

	public void setSystemExam(String systemExam) {
		this.systemExam = systemExam;
	}

	public String getPresentComplaintHistory() {
		return presentComplaintHistory;
	}

	public void setPresentComplaintHistory(String presentComplaintHistory) {
		this.presentComplaintHistory = presentComplaintHistory;
	}

	public String getMenstrualHistory() {
		return menstrualHistory;
	}

	public void setMenstrualHistory(String menstrualHistory) {
		this.menstrualHistory = menstrualHistory;
	}

	public String getObstetricHistory() {
		return obstetricHistory;
	}

	public void setObstetricHistory(String obstetricHistory) {
		this.obstetricHistory = obstetricHistory;
	}

	@Override
	public String toString() {
		return "ClinicalNotesJasperDetails [complaints=" + complaints + ", observations=" + observations
				+ ", investigations=" + investigations + ", diagnosis=" + diagnosis + ", notes=" + notes + ", diagrams="
				+ diagrams + ", vitalSigns=" + vitalSigns + ", presentComplaint=" + presentComplaint
				+ ", provisionalDiagnosis=" + provisionalDiagnosis + ", generalExam=" + generalExam + ", systemExam="
				+ systemExam + ", presentComplaintHistory=" + presentComplaintHistory + ", menstrualHistory="
				+ menstrualHistory + ", obstetricHistory=" + obstetricHistory + "]";
	}

}
