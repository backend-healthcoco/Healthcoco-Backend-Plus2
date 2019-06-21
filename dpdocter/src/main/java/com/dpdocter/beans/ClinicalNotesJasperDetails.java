package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.mongodb.DBObject;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ClinicalNotesJasperDetails {

	private String title;

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

	private String noseExam;

	private String oralCavityThroatExam;

	private String indirectLarygoscopyExam;

	private String neckExam;

	private String earsExam;

	private String pcNose;

	private String pcOralCavity;

	private String pcThroat;

	private String pcEars;

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

	private String procedureNote;
	
	private String pastHistory;

	private String familyHistory;

	private String personalHistoryTobacco;
	private String personalHistoryAlcohol;
	private String personalHistorySmoking;
	private String personalHistoryDiet;
	private String personalHistoryOccupation;
	private String generalHistoryDrugs;
	private String generalHistoryMedicine;
	private String generalHistoryAllergies;
	private String generalHistorySurgical;
	private String painScale;
	private String priorConsultations;

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

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public List<DBObject> getDiagrams() {
		return diagrams;
	}

	public void setDiagrams(List<DBObject> diagrams) {
		this.diagrams = diagrams;
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

	public String getProcedureNote() {
		return procedureNote;
	}

	public void setProcedureNote(String procedureNote) {
		this.procedureNote = procedureNote;
	}

	public String getPastHistory() {
		return pastHistory;
	}

	public void setPastHistory(String pastHistory) {
		this.pastHistory = pastHistory;
	}

	public String getFamilyHistory() {
		return familyHistory;
	}

	public void setFamilyHistory(String familyHistory) {
		this.familyHistory = familyHistory;
	}

	public String getPersonalHistoryTobacco() {
		return personalHistoryTobacco;
	}

	public void setPersonalHistoryTobacco(String personalHistoryTobacco) {
		this.personalHistoryTobacco = personalHistoryTobacco;
	}

	public String getPersonalHistoryAlcohol() {
		return personalHistoryAlcohol;
	}

	public void setPersonalHistoryAlcohol(String personalHistoryAlcohol) {
		this.personalHistoryAlcohol = personalHistoryAlcohol;
	}

	public String getPersonalHistorySmoking() {
		return personalHistorySmoking;
	}

	public void setPersonalHistorySmoking(String personalHistorySmoking) {
		this.personalHistorySmoking = personalHistorySmoking;
	}

	public String getPersonalHistoryDiet() {
		return personalHistoryDiet;
	}

	public void setPersonalHistoryDiet(String personalHistoryDiet) {
		this.personalHistoryDiet = personalHistoryDiet;
	}

	public String getPersonalHistoryOccupation() {
		return personalHistoryOccupation;
	}

	public void setPersonalHistoryOccupation(String personalHistoryOccupation) {
		this.personalHistoryOccupation = personalHistoryOccupation;
	}

	public String getGeneralHistoryDrugs() {
		return generalHistoryDrugs;
	}

	public void setGeneralHistoryDrugs(String generalHistoryDrugs) {
		this.generalHistoryDrugs = generalHistoryDrugs;
	}

	public String getGeneralHistoryMedicine() {
		return generalHistoryMedicine;
	}

	public void setGeneralHistoryMedicine(String generalHistoryMedicine) {
		this.generalHistoryMedicine = generalHistoryMedicine;
	}

	public String getGeneralHistoryAllergies() {
		return generalHistoryAllergies;
	}

	public void setGeneralHistoryAllergies(String generalHistoryAllergies) {
		this.generalHistoryAllergies = generalHistoryAllergies;
	}

	public String getGeneralHistorySurgical() {
		return generalHistorySurgical;
	}

	public void setGeneralHistorySurgical(String generalHistorySurgical) {
		this.generalHistorySurgical = generalHistorySurgical;
	}

	public String getPainScale() {
		return painScale;
	}

	public void setPainScale(String painScale) {
		this.painScale = painScale;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "ClinicalNotesJasperDetails [title=" + title + ", complaints=" + complaints + ", observations="
				+ observations + ", investigations=" + investigations + ", diagnosis=" + diagnosis + ", notes=" + notes
				+ ", diagrams=" + diagrams + ", vitalSigns=" + vitalSigns + ", presentComplaint=" + presentComplaint
				+ ", provisionalDiagnosis=" + provisionalDiagnosis + ", generalExam=" + generalExam + ", systemExam="
				+ systemExam + ", noseExam=" + noseExam + ", oralCavityThroatExam=" + oralCavityThroatExam
				+ ", indirectLarygoscopyExam=" + indirectLarygoscopyExam + ", neckExam=" + neckExam + ", earsExam="
				+ earsExam + ", pcNose=" + pcNose + ", pcOralCavity=" + pcOralCavity + ", pcThroat=" + pcThroat
				+ ", pcEars=" + pcEars + ", presentComplaintHistory=" + presentComplaintHistory + ", menstrualHistory="
				+ menstrualHistory + ", obstetricHistory=" + obstetricHistory + ", indicationOfUSG=" + indicationOfUSG
				+ ", pv=" + pv + ", pa=" + pa + ", ps=" + ps + ", ecgDetails=" + ecgDetails + ", xRayDetails="
				+ xRayDetails + ", echo=" + echo + ", holter=" + holter + ", lmp=" + lmp + ", edd=" + edd
				+ ", noOfChildren=" + noOfChildren + ", procedureNote=" + procedureNote + ", pastHistory=" + pastHistory
				+ ", familyHistory=" + familyHistory + ", personalHistoryTobacco=" + personalHistoryTobacco
				+ ", personalHistoryAlcohol=" + personalHistoryAlcohol + ", personalHistorySmoking="
				+ personalHistorySmoking + ", personalHistoryDiet=" + personalHistoryDiet
				+ ", personalHistoryOccupation=" + personalHistoryOccupation + ", generalHistoryDrugs="
				+ generalHistoryDrugs + ", generalHistoryMedicine=" + generalHistoryMedicine
				+ ", generalHistoryAllergies=" + generalHistoryAllergies + ", generalHistorySurgical="
				+ generalHistorySurgical + ", painScale=" + painScale + "]";
	}

	public String getPriorConsultations() {
		return priorConsultations;
	}

	public void setPriorConsultations(String priorConsultations) {
		this.priorConsultations = priorConsultations;
	}

}
