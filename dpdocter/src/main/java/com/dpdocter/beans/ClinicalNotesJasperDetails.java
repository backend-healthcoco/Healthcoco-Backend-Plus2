package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

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

	private String indicationOfUSG;
	
	private String pv;
	
	private String pa;
	
	private String ps;
	
	private String ecgDetails;
	
	private String xRayDetails;
	
	private String echo;
	
	private String holter;

    private String lmp;
	
	private String edd;
	
	private String noOfChildren;
	
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

	public String getIndicationOfUSG() {
		return indicationOfUSG;
	}

	public void setIndicationOfUSG(String indicationOfUSG) {
		this.indicationOfUSG = indicationOfUSG;
	}

	public String getPv() {
		return pv;
	}

	public void setPv(String pv) {
		this.pv = pv;
	}

	public String getPa() {
		return pa;
	}

	public void setPa(String pa) {
		this.pa = pa;
	}

	public String getPs() {
		return ps;
	}

	public void setPs(String ps) {
		this.ps = ps;
	}

	public String getEcgDetails() {
		return ecgDetails;
	}

	public void setEcgDetails(String ecgDetails) {
		this.ecgDetails = ecgDetails;
	}

	public String getxRayDetails() {
		return xRayDetails;
	}

	public void setxRayDetails(String xRayDetails) {
		this.xRayDetails = xRayDetails;
	}

	public String getEcho() {
		return echo;
	}

	public void setEcho(String echo) {
		this.echo = echo;
	}

	public String getHolter() {
		return holter;
	}

	public void setHolter(String holter) {
		this.holter = holter;
	}

	public String getLmp() {
		return lmp;
	}

	public void setLmp(String lmp) {
		this.lmp = lmp;
	}

	public String getEdd() {
		return edd;
	}

	public void setEdd(String edd) {
		this.edd = edd;
	}

	public String getNoOfChildren() {
		return noOfChildren;
	}

	public void setNoOfChildren(String noOfChildren) {
		this.noOfChildren = noOfChildren;
	}

	@Override
	public String toString() {
		return "ClinicalNotesJasperDetails [complaints=" + complaints + ", observations=" + observations
				+ ", investigations=" + investigations + ", diagnosis=" + diagnosis + ", notes=" + notes + ", diagrams="
				+ diagrams + ", vitalSigns=" + vitalSigns + ", presentComplaint=" + presentComplaint
				+ ", provisionalDiagnosis=" + provisionalDiagnosis + ", generalExam=" + generalExam + ", systemExam="
				+ systemExam + ", presentComplaintHistory=" + presentComplaintHistory + ", menstrualHistory="
				+ menstrualHistory + ", obstetricHistory=" + obstetricHistory + ", indicationOfUSG=" + indicationOfUSG
				+ ", pv=" + pv + ", pa=" + pa + ", ps=" + ps + ", ecgDetails=" + ecgDetails + ", xRayDetails="
				+ xRayDetails + ", echo=" + echo + ", holter=" + holter + ", lmp=" + lmp + ", edd=" + edd
				+ ", noOfChildren=" + noOfChildren + "]";
	}
}
