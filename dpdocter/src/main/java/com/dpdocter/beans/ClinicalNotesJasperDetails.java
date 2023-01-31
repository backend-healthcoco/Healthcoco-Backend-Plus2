package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import com.dpdocter.response.EyeTestJasperResponse;
import com.dpdocter.response.EyeVisualAcuitiesJasperResponse;
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
	private String historyOfPresentIllness;
	private String otNotes;
	private String site;
	private String nature;
	private String physioExaminationPastHistory;

	private String painType;
	private String nprs;
	private String onRest;
	private String onActivity;
	private String painAggrevatingFactor;
	private String painReleavingFactor;
	private String swell;
	private Boolean swellValue = false;
	private String spasm;
	private Boolean spasmValue = false;
	private String tenderness;
	private Boolean tendernessValue = false;
	private String shoulderFlexion;
	private String shoulderExtension;
	private String shoulderAbduction;
	private String shoulderIntegerRotation;
	private String shoulderExternalRotation;
	private String elbowHexion;
	private String elbowExtension;
	private String elbowSupination;
	private String elbowPronotion;
	private String wristHexion;
	private String wristExtension;
	private String wristRadial_UlnarDeviation;
	private String hipJointHexion;
	private String hipJointExtension;
	private String hipJointAbduction;
	private String kneeJointsHexion;
	private String kneeJointsExtension;
	private String anklePlantarlexion;
	private String ankleDorsiflexion;
	private String inspectionOfPartPosture;
	private String manualMuscleTesting;
	private String treatment;
	private Boolean emptyCanTest = false;
	private Boolean fullCanTest = false;
	private Boolean hornBlowerTest = false;
	private Boolean infrasplnatureTest = false;
	private Boolean speedTest = false;
	private Boolean yergasonsTest = false;
	private Boolean impingmentTest = false;
	private Boolean oBrionsTest = false;
	private Boolean thomasTest;
	private Boolean obersTest;
	private String fungusLeftEye;
	private String fungusRightEye;
	private String entity_IOP;
	private String leftVisionPR;
	private String rightVisionPR;
	private String entityPr;
	private String leftEyeVisionPR;
	private String rightEyeVisionPR;
	private EyeTestJasperResponse leftEyeTest;
	private EyeTestJasperResponse rightEyeTest;
	private EyeVisualAcuitiesJasperResponse rightVisualAcuities;
	private EyeVisualAcuitiesJasperResponse leftVisualAcuities;
	private List<DBObject> eyeExamination;
	private List<DBObject> eyeInvestigation;
	private String vaccinationHistory;

	
	public String getVaccinationHistory() {
		return vaccinationHistory;
	}

	public void setVaccinationHistory(String vaccinationHistory) {
		this.vaccinationHistory = vaccinationHistory;
	}

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

	public String getPriorConsultations() {
		return priorConsultations;
	}

	public void setPriorConsultations(String priorConsultations) {
		this.priorConsultations = priorConsultations;
	}

	public String getHistoryOfPresentIllness() {
		return historyOfPresentIllness;
	}

	public void setHistoryOfPresentIllness(String historyOfPresentIllness) {
		this.historyOfPresentIllness = historyOfPresentIllness;
	}

	public String getOtNotes() {
		return otNotes;
	}

	public void setOtNotes(String otNotes) {
		this.otNotes = otNotes;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	public String getOnRest() {
		return onRest;
	}

	public void setOnRest(String onRest) {
		this.onRest = onRest;
	}

	public String getOnActivity() {
		return onActivity;
	}

	public void setOnActivity(String onActivity) {
		this.onActivity = onActivity;
	}

	public String getSwell() {
		return swell;
	}

	public void setSwell(String swell) {
		this.swell = swell;
	}

	public Boolean getSwellValue() {
		return swellValue;
	}

	public void setSwellValue(Boolean swellValue) {
		this.swellValue = swellValue;
	}

	public String getSpasm() {
		return spasm;
	}

	public void setSpasm(String spasm) {
		this.spasm = spasm;
	}

	public Boolean getSpasmValue() {
		return spasmValue;
	}

	public void setSpasmValue(Boolean spasmValue) {
		this.spasmValue = spasmValue;
	}

	public Boolean getTendernessValue() {
		return tendernessValue;
	}

	public void setTendernessValue(Boolean tendernessValue) {
		this.tendernessValue = tendernessValue;
	}

	public String getTenderness() {
		return tenderness;
	}

	public void setTenderness(String tenderness) {
		this.tenderness = tenderness;
	}

	public String getShoulderFlexion() {
		return shoulderFlexion;
	}

	public void setShoulderFlexion(String shoulderFlexion) {
		this.shoulderFlexion = shoulderFlexion;
	}

	public String getShoulderExtension() {
		return shoulderExtension;
	}

	public void setShoulderExtension(String shoulderExtension) {
		this.shoulderExtension = shoulderExtension;
	}

	public String getShoulderAbduction() {
		return shoulderAbduction;
	}

	public void setShoulderAbduction(String shoulderAbduction) {
		this.shoulderAbduction = shoulderAbduction;
	}

	public String getShoulderIntegerRotation() {
		return shoulderIntegerRotation;
	}

	public void setShoulderIntegerRotation(String shoulderIntegerRotation) {
		this.shoulderIntegerRotation = shoulderIntegerRotation;
	}

	public String getShoulderExternalRotation() {
		return shoulderExternalRotation;
	}

	public void setShoulderExternalRotation(String shoulderExternalRotation) {
		this.shoulderExternalRotation = shoulderExternalRotation;
	}

	public String getElbowHexion() {
		return elbowHexion;
	}

	public void setElbowHexion(String elbowHexion) {
		this.elbowHexion = elbowHexion;
	}

	public String getElbowExtension() {
		return elbowExtension;
	}

	public void setElbowExtension(String elbowExtension) {
		this.elbowExtension = elbowExtension;
	}

	public String getElbowSupination() {
		return elbowSupination;
	}

	public void setElbowSupination(String elbowSupination) {
		this.elbowSupination = elbowSupination;
	}

	public String getElbowPronotion() {
		return elbowPronotion;
	}

	public void setElbowPronotion(String elbowPronotion) {
		this.elbowPronotion = elbowPronotion;
	}

	public String getWristHexion() {
		return wristHexion;
	}

	public void setWristHexion(String wristHexion) {
		this.wristHexion = wristHexion;
	}

	public String getWristExtension() {
		return wristExtension;
	}

	public void setWristExtension(String wristExtension) {
		this.wristExtension = wristExtension;
	}

	public String getWristRadial_UlnarDeviation() {
		return wristRadial_UlnarDeviation;
	}

	public void setWristRadial_UlnarDeviation(String wristRadial_UlnarDeviation) {
		this.wristRadial_UlnarDeviation = wristRadial_UlnarDeviation;
	}

	public String getHipJointHexion() {
		return hipJointHexion;
	}

	public void setHipJointHexion(String hipJointHexion) {
		this.hipJointHexion = hipJointHexion;
	}

	public String getHipJointExtension() {
		return hipJointExtension;
	}

	public void setHipJointExtension(String hipJointExtension) {
		this.hipJointExtension = hipJointExtension;
	}

	public String getHipJointAbduction() {
		return hipJointAbduction;
	}

	public void setHipJointAbduction(String hipJointAbduction) {
		this.hipJointAbduction = hipJointAbduction;
	}

	public String getKneeJointsHexion() {
		return kneeJointsHexion;
	}

	public void setKneeJointsHexion(String kneeJointsHexion) {
		this.kneeJointsHexion = kneeJointsHexion;
	}

	public String getKneeJointsExtension() {
		return kneeJointsExtension;
	}

	public void setKneeJointsExtension(String kneeJointsExtension) {
		this.kneeJointsExtension = kneeJointsExtension;
	}

	public String getAnklePlantarlexion() {
		return anklePlantarlexion;
	}

	public void setAnklePlantarlexion(String anklePlantarlexion) {
		this.anklePlantarlexion = anklePlantarlexion;
	}

	public String getAnkleDorsiflexion() {
		return ankleDorsiflexion;
	}

	public void setAnkleDorsiflexion(String ankleDorsiflexion) {
		this.ankleDorsiflexion = ankleDorsiflexion;
	}

	public String getManualMuscleTesting() {
		return manualMuscleTesting;
	}

	public void setManualMuscleTesting(String manualMuscleTesting) {
		this.manualMuscleTesting = manualMuscleTesting;
	}

	public String getTreatment() {
		return treatment;
	}

	public void setTreatment(String treatment) {
		this.treatment = treatment;
	}

	public Boolean getEmptyCanTest() {
		return emptyCanTest;
	}

	public void setEmptyCanTest(Boolean emptyCanTest) {
		this.emptyCanTest = emptyCanTest;
	}

	public Boolean getFullCanTest() {
		return fullCanTest;
	}

	public void setFullCanTest(Boolean fullCanTest) {
		this.fullCanTest = fullCanTest;
	}

	public Boolean getHornBlowerTest() {
		return hornBlowerTest;
	}

	public void setHornBlowerTest(Boolean hornBlowerTest) {
		this.hornBlowerTest = hornBlowerTest;
	}

	public Boolean getInfrasplnatureTest() {
		return infrasplnatureTest;
	}

	public void setInfrasplnatureTest(Boolean infrasplnatureTest) {
		this.infrasplnatureTest = infrasplnatureTest;
	}

	public Boolean getSpeedTest() {
		return speedTest;
	}

	public void setSpeedTest(Boolean speedTest) {
		this.speedTest = speedTest;
	}

	public Boolean getYergasonsTest() {
		return yergasonsTest;
	}

	public void setYergasonsTest(Boolean yergasonsTest) {
		this.yergasonsTest = yergasonsTest;
	}

	public Boolean getImpingmentTest() {
		return impingmentTest;
	}

	public void setImpingmentTest(Boolean impingmentTest) {
		this.impingmentTest = impingmentTest;
	}

	public Boolean getThomasTest() {
		return thomasTest;
	}

	public void setThomasTest(Boolean thomasTest) {
		this.thomasTest = thomasTest;
	}

	public Boolean getObersTest() {
		return obersTest;
	}

	public void setObersTest(Boolean obersTest) {
		this.obersTest = obersTest;
	}

	public String getPhysioExaminationPastHistory() {
		return physioExaminationPastHistory;
	}

	public void setPhysioExaminationPastHistory(String physioExaminationPastHistory) {
		this.physioExaminationPastHistory = physioExaminationPastHistory;
	}


	public String getNprs() {
		return nprs;
	}

	public void setNprs(String nprs) {
		this.nprs = nprs;
	}

	public String getInspectionOfPartPosture() {
		return inspectionOfPartPosture;
	}

	public void setInspectionOfPartPosture(String inspectionOfPartPosture) {
		this.inspectionOfPartPosture = inspectionOfPartPosture;
	}

	public Boolean getoBrionsTest() {
		return oBrionsTest;
	}

	public void setoBrionsTest(Boolean oBrionsTest) {
		this.oBrionsTest = oBrionsTest;
	}

	public String getFungusLeftEye() {
		return fungusLeftEye;
	}

	public void setFungusLeftEye(String fungusLeftEye) {
		this.fungusLeftEye = fungusLeftEye;
	}

	public String getFungusRightEye() {
		return fungusRightEye;
	}

	public void setFungusRightEye(String fungusRightEye) {
		this.fungusRightEye = fungusRightEye;
	}

	public String getEntity_IOP() {
		return entity_IOP;
	}

	public void setEntity_IOP(String entity_IOP) {
		this.entity_IOP = entity_IOP;
	}

	public String getLeftVisionPR() {
		return leftVisionPR;
	}

	public void setLeftVisionPR(String leftVisionPR) {
		this.leftVisionPR = leftVisionPR;
	}

	public String getRightVisionPR() {
		return rightVisionPR;
	}

	public void setRightVisionPR(String rightVisionPR) {
		this.rightVisionPR = rightVisionPR;
	}

	public String getEntityPr() {
		return entityPr;
	}

	public void setEntityPr(String entityPr) {
		this.entityPr = entityPr;
	}

	public String getLeftEyeVisionPR() {
		return leftEyeVisionPR;
	}

	public void setLeftEyeVisionPR(String leftEyeVisionPR) {
		this.leftEyeVisionPR = leftEyeVisionPR;
	}

	public String getRightEyeVisionPR() {
		return rightEyeVisionPR;
	}

	public void setRightEyeVisionPR(String rightEyeVisionPR) {
		this.rightEyeVisionPR = rightEyeVisionPR;
	}

	public EyeTestJasperResponse getLeftEyeTest() {
		return leftEyeTest;
	}

	public void setLeftEyeTest(EyeTestJasperResponse leftEyeTest) {
		this.leftEyeTest = leftEyeTest;
	}

	public EyeTestJasperResponse getRightEyeTest() {
		return rightEyeTest;
	}

	public void setRightEyeTest(EyeTestJasperResponse rightEyeTest) {
		this.rightEyeTest = rightEyeTest;
	}

	public EyeVisualAcuitiesJasperResponse getRightVisualAcuities() {
		return rightVisualAcuities;
	}

	public void setRightVisualAcuities(EyeVisualAcuitiesJasperResponse rightVisualAcuities) {
		this.rightVisualAcuities = rightVisualAcuities;
	}

	public EyeVisualAcuitiesJasperResponse getLeftVisualAcuities() {
		return leftVisualAcuities;
	}

	public void setLeftVisualAcuities(EyeVisualAcuitiesJasperResponse leftVisualAcuities) {
		this.leftVisualAcuities = leftVisualAcuities;
	}
	
	
	public String getPainType() {
		return painType;
	}

	public void setPainType(String painType) {
		this.painType = painType;
	}

	public List<DBObject> getEyeExamination() {
		return eyeExamination;
	}

	public void setEyeExamination(List<DBObject> eyeExamination) {
		this.eyeExamination = eyeExamination;
	}

	public String getPainAggrevatingFactor() {
		return painAggrevatingFactor;
	}

	public void setPainAggrevatingFactor(String painAggrevatingFactor) {
		this.painAggrevatingFactor = painAggrevatingFactor;
	}

	public String getPainReleavingFactor() {
		return painReleavingFactor;
	}

	public void setPainReleavingFactor(String painReleavingFactor) {
		this.painReleavingFactor = painReleavingFactor;
	}

	public List<DBObject> getEyeInvestigation() {
		return eyeInvestigation;
	}

	public void setEyeInvestigation(List<DBObject> eyeInvestigation) {
		this.eyeInvestigation = eyeInvestigation;
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
}
