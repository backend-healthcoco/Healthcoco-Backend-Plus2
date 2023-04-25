package com.dpdocter.beans;

import com.dpdocter.enums.Range;

import common.util.web.JacksonUtil;

public class ClinicalNotesDynamicField {

	private String note = Range.BOTH.getRange();

	private String observation = Range.BOTH.getRange();

	private String investigation = Range.BOTH.getRange();

	private String diagnosis = Range.BOTH.getRange();

	private String provisionalDiagnosis = Range.BOTH.getRange();

	private String generalExam = Range.BOTH.getRange();

	private String systemExam = Range.BOTH.getRange();

	private String complaint = Range.BOTH.getRange();

	private String presentComplaint = Range.BOTH.getRange();

	private String procedureNote = Range.BOTH.getRange();

	private String presentComplaintHistory = Range.BOTH.getRange();

	private String menstrualHistory = Range.BOTH.getRange();

	private String obstetricHistory = Range.BOTH.getRange();

	private String indicationOfUSG = Range.BOTH.getRange();

	private String pv = Range.BOTH.getRange();

	private String pa = Range.BOTH.getRange();

	private String ps = Range.BOTH.getRange();

	private String ecgDetails = Range.BOTH.getRange();

	private String xRayDetails = Range.BOTH.getRange();

	private String echo = Range.BOTH.getRange();

	private String holter = Range.BOTH.getRange();

	private String pcNose = Range.BOTH.getRange();

	private String pcOralCavity = Range.BOTH.getRange();

	private String pcThroat = Range.BOTH.getRange();

	private String pcEars = Range.BOTH.getRange();

	private String noseExam = Range.BOTH.getRange();

	private String oralCavityThroatExam = Range.BOTH.getRange();

	private String indirectLarygoscopyExam = Range.BOTH.getRange();

	private String neckExam = Range.BOTH.getRange();

	private String earsExam = Range.BOTH.getRange();

	public ClinicalNotesDynamicField() {
		this.note = Range.BOTH.getRange();
		this.observation = Range.BOTH.getRange();
		this.investigation = Range.BOTH.getRange();
		this.diagnosis = Range.BOTH.getRange();
		this.provisionalDiagnosis = Range.BOTH.getRange();
		this.generalExam = Range.BOTH.getRange();
		this.systemExam = Range.BOTH.getRange();
		this.complaint = Range.BOTH.getRange();
		this.presentComplaint = Range.BOTH.getRange();
		this.procedureNote = Range.BOTH.getRange();
		this.presentComplaintHistory = Range.BOTH.getRange();
		this.menstrualHistory = Range.BOTH.getRange();
		this.obstetricHistory = Range.BOTH.getRange();
		this.indicationOfUSG = Range.BOTH.getRange();
		this.pv = Range.BOTH.getRange();
		this.pa = Range.BOTH.getRange();
		this.ps = Range.BOTH.getRange();
		this.ecgDetails = Range.BOTH.getRange();
		this.xRayDetails = Range.BOTH.getRange();
		this.echo = Range.BOTH.getRange();
		this.holter = Range.BOTH.getRange();
		this.pcNose = Range.BOTH.getRange();
		this.pcOralCavity = Range.BOTH.getRange();
		this.pcThroat = Range.BOTH.getRange();
		this.pcEars = Range.BOTH.getRange();
		this.noseExam = Range.BOTH.getRange();
		this.oralCavityThroatExam = Range.BOTH.getRange();
		this.indirectLarygoscopyExam = Range.BOTH.getRange();
		this.neckExam = Range.BOTH.getRange();
		this.earsExam = Range.BOTH.getRange();
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
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

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
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

	public String getComplaint() {
		return complaint;
	}

	public void setComplaint(String complaint) {
		this.complaint = complaint;
	}

	public String getPresentComplaint() {
		return presentComplaint;
	}

	public void setPresentComplaint(String presentComplaint) {
		this.presentComplaint = presentComplaint;
	}

	public String getProcedureNote() {
		return procedureNote;
	}

	public void setProcedureNote(String procedureNote) {
		this.procedureNote = procedureNote;
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

	public String getPcNose() {
		return pcNose;
	}

	public void setPcNose(String pcNose) {
		this.pcNose = pcNose;
	}

	public String getPcOralCavity() {
		return pcOralCavity;
	}

	public void setPcOralCavity(String pcOralCavity) {
		this.pcOralCavity = pcOralCavity;
	}

	public String getPcThroat() {
		return pcThroat;
	}

	public void setPcThroat(String pcThroat) {
		this.pcThroat = pcThroat;
	}

	public String getPcEars() {
		return pcEars;
	}

	public void setPcEars(String pcEars) {
		this.pcEars = pcEars;
	}

	public String getNoseExam() {
		return noseExam;
	}

	public void setNoseExam(String noseExam) {
		this.noseExam = noseExam;
	}

	public String getOralCavityThroatExam() {
		return oralCavityThroatExam;
	}

	public void setOralCavityThroatExam(String oralCavityThroatExam) {
		this.oralCavityThroatExam = oralCavityThroatExam;
	}

	public String getIndirectLarygoscopyExam() {
		return indirectLarygoscopyExam;
	}

	public void setIndirectLarygoscopyExam(String indirectLarygoscopyExam) {
		this.indirectLarygoscopyExam = indirectLarygoscopyExam;
	}

	public String getNeckExam() {
		return neckExam;
	}

	public void setNeckExam(String neckExam) {
		this.neckExam = neckExam;
	}

	public String getEarsExam() {
		return earsExam;
	}

	public void setEarsExam(String earsExam) {
		this.earsExam = earsExam;
	}

	@Override
	public String toString() {
		return "ClinicalNotesDynamicField [note=" + note + ", observation=" + observation + ", investigation="
				+ investigation + ", diagnosis=" + diagnosis + ", provisionalDiagnosis=" + provisionalDiagnosis
				+ ", generalExam=" + generalExam + ", systemExam=" + systemExam + ", complaint=" + complaint
				+ ", presentComplaint=" + presentComplaint + ", procedureNote=" + procedureNote
				+ ", presentComplaintHistory=" + presentComplaintHistory + ", menstrualHistory=" + menstrualHistory
				+ ", obstetricHistory=" + obstetricHistory + ", indicationOfUSG=" + indicationOfUSG + ", pv=" + pv
				+ ", pa=" + pa + ", ps=" + ps + ", ecgDetails=" + ecgDetails + ", xRayDetails=" + xRayDetails
				+ ", echo=" + echo + ", holter=" + holter + ", pcNose=" + pcNose + ", pcOralCavity=" + pcOralCavity
				+ ", pcThroat=" + pcThroat + ", pcEars=" + pcEars + ", noseExam=" + noseExam + ", oralCavityThroatExam="
				+ oralCavityThroatExam + ", indirectLarygoscopyExam=" + indirectLarygoscopyExam + ", neckExam="
				+ neckExam + ", earsExam=" + earsExam + "]";
	}

	public static void main(String[] args) {
		System.out.println(JacksonUtil.obj2Json(new ClinicalNotesDynamicField()));
	}

}
