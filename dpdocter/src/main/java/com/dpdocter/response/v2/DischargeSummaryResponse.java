package com.dpdocter.response.v2;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.beans.FlowSheet;
import com.dpdocter.beans.VitalSigns;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.beans.v2.Prescription;
import com.dpdocter.collections.GenericCollection;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DischargeSummaryResponse extends GenericCollection {

	private String id;
	private String patientId;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private Prescription prescriptions;
	private String appointmentId;
	private String diagnosis;
	private String pastHistory;
	private String familyHistory;
	private String personalHistory;
	private String complaint;
	private String presentComplaint;
	private String presentComplaintHistory;
	private String menstrualHistory;
	private String obstetricHistory;
	private String generalExam;
	private String systemExam;
	private String observation;
	private String investigation;
	private String pa;
	private String ps;
	private String pv;
	private String echo;
	private String xRayDetails;
	private String operationNotes;
	private String treatmentsGiven;
	private WorkingHours time;
	private Date fromDate;
	private String uniqueEmrId;
	private Date admissionDate;
	private Date dischargeDate;
	private String labourNotes;
	private String babyWeight;
	private String babyNotes;
	private String conditionsAtDischarge;
	private String summary;
	private Boolean discarded = false;
	private String indicationOfUSG;
	private String ecgDetails;
	private String holter;
	private String procedureNote;
	private String doctorIncharge;
	private VitalSigns vitalSigns;
	private Date operationDate;
	private String surgeon;
	private String anesthetist;
	private String implant;
	private String cement;
	private String operationName;
	private String lmp;
	private String edd;
	private Date surgeryDate;
	private String surgeryNotes;
	private String treatingConsultant;
	private String anesthesia;
	private String consultantDoctor;
	private String assistantDoctor;
	private String treatmentAdviceForMother;
	private String treatmentAdviceForBaby;
	private String pediatricianName;
	private List<FlowSheet> flowSheets;
	private List<String> diagrams;
	private String timeOfEntryInOt;
	private String timeOfExitFromOt;
	private String bloodLoss;
	private String ageOnAdmission;
	private String ageOnDischarge;
	private String weightOnAdmission;
	private String weightOnDischarge;
	private String timeOfAdmission;
	private String timeOfDischarge;
	private String timeOfOperation;
	private String referenceName;
	private String dischargeStatus;
	private String dischargeOutcome;
	private String bedLog;
	private String hospitalCourse;
	
	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public Date getOperationDate() {
		return operationDate;
	}

	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}

	public String getSurgeon() {
		return surgeon;
	}

	public void setSurgeon(String surgeon) {
		this.surgeon = surgeon;
	}

	public String getAnesthetist() {
		return anesthetist;
	}

	public void setAnesthetist(String anesthetist) {
		this.anesthetist = anesthetist;
	}

	public String getImplant() {
		return implant;
	}

	public void setImplant(String implant) {
		this.implant = implant;
	}

	public String getCement() {
		return cement;
	}

	public void setCement(String cement) {
		this.cement = cement;
	}

	public VitalSigns getVitalSigns() {
		return vitalSigns;
	}

	public void setVitalSigns(VitalSigns vitalSigns) {
		this.vitalSigns = vitalSigns;
	}

	public String getIndicationOfUSG() {
		return indicationOfUSG;
	}

	public void setIndicationOfUSG(String indicationOfUSG) {
		this.indicationOfUSG = indicationOfUSG;
	}

	public String getEcgDetails() {
		return ecgDetails;
	}

	public void setEcgDetails(String ecgDetails) {
		this.ecgDetails = ecgDetails;
	}

	public String getHolter() {
		return holter;
	}

	public void setHolter(String holter) {
		this.holter = holter;
	}

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

	public Prescription getPrescriptions() {
		return prescriptions;
	}

	public void setPrescriptions(Prescription prescriptions) {
		this.prescriptions = prescriptions;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
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

	public String getPersonalHistory() {
		return personalHistory;
	}

	public void setPersonalHistory(String personalHistory) {
		this.personalHistory = personalHistory;
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

	public String getPa() {
		return pa;
	}

	public void setPa(String pa) {
		this.pa = pa;
	}

	public String getPv() {
		return pv;
	}

	public void setPv(String pv) {
		this.pv = pv;
	}

	public String getEcho() {
		return echo;
	}

	public void setEcho(String echo) {
		this.echo = echo;
	}

	public String getxRayDetails() {
		return xRayDetails;
	}

	public void setxRayDetails(String xRayDetails) {
		this.xRayDetails = xRayDetails;
	}

	public String getOperationNotes() {
		return operationNotes;
	}

	public void setOperationNotes(String operationNotes) {
		this.operationNotes = operationNotes;
	}

	public Date getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}

	public Date getDischargeDate() {
		return dischargeDate;
	}

	public void setDischargeDate(Date dischargeDate) {
		this.dischargeDate = dischargeDate;
	}

	public String getLabourNotes() {
		return labourNotes;
	}

	public void setLabourNotes(String labourNotes) {
		this.labourNotes = labourNotes;
	}

	public String getBabyWeight() {
		return babyWeight;
	}

	public void setBabyWeight(String babyWeight) {
		this.babyWeight = babyWeight;
	}

	public String getBabyNotes() {
		return babyNotes;
	}

	public void setBabyNotes(String babyNotes) {
		this.babyNotes = babyNotes;
	}

	public String getConditionsAtDischarge() {
		return conditionsAtDischarge;
	}

	public String getTreatmentsGiven() {
		return treatmentsGiven;
	}

	public void setTreatmentsGiven(String treatmentsGiven) {
		this.treatmentsGiven = treatmentsGiven;
	}

	public void setConditionsAtDischarge(String conditionsAtDischarge) {
		this.conditionsAtDischarge = conditionsAtDischarge;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getPs() {
		return ps;
	}

	public void setPs(String ps) {
		this.ps = ps;
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

	public String getPresentComplaintHistory() {
		return presentComplaintHistory;
	}

	public void setPresentComplaintHistory(String presentComplaintHistory) {
		this.presentComplaintHistory = presentComplaintHistory;
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

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
	}

	@Override
	public String toString() {
		return "DischargeSummaryResponse [id=" + id + ", patientId=" + patientId + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", prescriptions=" + prescriptions
				+ ", appointmentId=" + appointmentId + ", diagnosis=" + diagnosis + ", pastHistory=" + pastHistory
				+ ", familyHistory=" + familyHistory + ", personalHistory=" + personalHistory + ", complaint="
				+ complaint + ", presentComplaint=" + presentComplaint + ", presentComplaintHistory="
				+ presentComplaintHistory + ", menstrualHistory=" + menstrualHistory + ", obstetricHistory="
				+ obstetricHistory + ", generalExam=" + generalExam + ", systemExam=" + systemExam + ", observation="
				+ observation + ", investigation=" + investigation + ", pa=" + pa + ", ps=" + ps + ", pv=" + pv
				+ ", echo=" + echo + ", xRayDetails=" + xRayDetails + ", operationNotes=" + operationNotes
				+ ", treatmentsGiven=" + treatmentsGiven + ", time=" + time + ", fromDate=" + fromDate
				+ ", uniqueEmrId=" + uniqueEmrId + ", admissionDate=" + admissionDate + ", dischargeDate="
				+ dischargeDate + ", labourNotes=" + labourNotes + ", babyWeight=" + babyWeight + ", babyNotes="
				+ babyNotes + ", conditionsAtDischarge=" + conditionsAtDischarge + ", summary=" + summary
				+ ", discarded=" + discarded + ", indicationOfUSG=" + indicationOfUSG + ", ecgDetails=" + ecgDetails
				+ ", holter=" + holter + ", procedureNote=" + procedureNote + ", doctorIncharge=" + doctorIncharge
				+ ", vitalSigns=" + vitalSigns + ", operationDate=" + operationDate + ", surgeon=" + surgeon
				+ ", anesthetist=" + anesthetist + ", implant=" + implant + ", cement=" + cement + ", operationName="
				+ operationName + ", lmp=" + lmp + ", edd=" + edd + ", surgeryDate=" + surgeryDate + ", surgeryNotes="
				+ surgeryNotes + ", treatingConsultant=" + treatingConsultant + ", anesthesia=" + anesthesia
				+ ", consultantDoctor=" + consultantDoctor + ", assistantDoctor=" + assistantDoctor
				+ ", treatmentAdviceForMother=" + treatmentAdviceForMother + ", treatmentAdviceForBaby="
				+ treatmentAdviceForBaby + ", pediatricianName=" + pediatricianName + ", flowSheets=" + flowSheets
				+ ", diagrams=" + diagrams + ", timeOfEntryInOt=" + timeOfEntryInOt + ", timeOfExitFromOt="
				+ timeOfExitFromOt + ", bloodLoss=" + bloodLoss + ", ageOnAdmission=" + ageOnAdmission
				+ ", ageOnDischarge=" + ageOnDischarge + ", weightOnAdmission=" + weightOnAdmission
				+ ", weightOnDischarge=" + weightOnDischarge + ", timeOfAdmission=" + timeOfAdmission
				+ ", timeOfDischarge=" + timeOfDischarge + ", timeOfOperation=" + timeOfOperation + ", referenceName="
				+ referenceName + ", dischargeStatus=" + dischargeStatus + ", dischargeOutcome=" + dischargeOutcome
				+ ", bedLog=" + bedLog + ", hospitalCourse=" + hospitalCourse + "]";
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public WorkingHours getTime() {
		return time;
	}

	public void setTime(WorkingHours time) {
		this.time = time;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public String getProcedureNote() {
		return procedureNote;
	}

	public void setProcedureNote(String procedureNote) {
		this.procedureNote = procedureNote;
	}

	public String getDoctorIncharge() {
		return doctorIncharge;
	}

	public void setDoctorIncharge(String doctorIncharge) {
		this.doctorIncharge = doctorIncharge;
	}

	public List<FlowSheet> getFlowSheets() {
		return flowSheets;
	}

	public void setFlowSheets(List<FlowSheet> flowSheets) {
		this.flowSheets = flowSheets;
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

	public Date getSurgeryDate() {
		return surgeryDate;
	}

	public void setSurgeryDate(Date surgeryDate) {
		this.surgeryDate = surgeryDate;
	}

	public String getSurgeryNotes() {
		return surgeryNotes;
	}

	public void setSurgeryNotes(String surgeryNotes) {
		this.surgeryNotes = surgeryNotes;
	}

	public String getTreatingConsultant() {
		return treatingConsultant;
	}

	public void setTreatingConsultant(String treatingConsultant) {
		this.treatingConsultant = treatingConsultant;
	}

	public String getAnesthesia() {
		return anesthesia;
	}

	public void setAnesthesia(String anesthesia) {
		this.anesthesia = anesthesia;
	}

	public String getConsultantDoctor() {
		return consultantDoctor;
	}

	public void setConsultantDoctor(String consultantDoctor) {
		this.consultantDoctor = consultantDoctor;
	}

	public String getAssistantDoctor() {
		return assistantDoctor;
	}

	public void setAssistantDoctor(String assistantDoctor) {
		this.assistantDoctor = assistantDoctor;
	}

	public String getTreatmentAdviceForMother() {
		return treatmentAdviceForMother;
	}

	public void setTreatmentAdviceForMother(String treatmentAdviceForMother) {
		this.treatmentAdviceForMother = treatmentAdviceForMother;
	}

	public String getTreatmentAdviceForBaby() {
		return treatmentAdviceForBaby;
	}

	public void setTreatmentAdviceForBaby(String treatmentAdviceForBaby) {
		this.treatmentAdviceForBaby = treatmentAdviceForBaby;
	}

	public String getPediatricianName() {
		return pediatricianName;
	}

	public void setPediatricianName(String pediatricianName) {
		this.pediatricianName = pediatricianName;
	}

	public List<String> getDiagrams() {
		return diagrams;
	}

	public void setDiagrams(List<String> diagrams) {
		this.diagrams = diagrams;
	}

	public String getTimeOfEntryInOt() {
		return timeOfEntryInOt;
	}

	public void setTimeOfEntryInOt(String timeOfEntryInOt) {
		this.timeOfEntryInOt = timeOfEntryInOt;
	}

	public String getTimeOfExitFromOt() {
		return timeOfExitFromOt;
	}

	public void setTimeOfExitFromOt(String timeOfExitFromOt) {
		this.timeOfExitFromOt = timeOfExitFromOt;
	}

	public String getBloodLoss() {
		return bloodLoss;
	}

	public void setBloodLoss(String bloodLoss) {
		this.bloodLoss = bloodLoss;
	}

	public String getAgeOnAdmission() {
		return ageOnAdmission;
	}

	public void setAgeOnAdmission(String ageOnAdmission) {
		this.ageOnAdmission = ageOnAdmission;
	}

	public String getAgeOnDischarge() {
		return ageOnDischarge;
	}

	public void setAgeOnDischarge(String ageOnDischarge) {
		this.ageOnDischarge = ageOnDischarge;
	}

	public String getWeightOnAdmission() {
		return weightOnAdmission;
	}

	public void setWeightOnAdmission(String weightOnAdmission) {
		this.weightOnAdmission = weightOnAdmission;
	}

	public String getWeightOnDischarge() {
		return weightOnDischarge;
	}

	public void setWeightOnDischarge(String weightOnDischarge) {
		this.weightOnDischarge = weightOnDischarge;
	}

	public String getTimeOfAdmission() {
		return timeOfAdmission;
	}

	public void setTimeOfAdmission(String timeOfAdmission) {
		this.timeOfAdmission = timeOfAdmission;
	}

	public String getTimeOfDischarge() {
		return timeOfDischarge;
	}

	public void setTimeOfDischarge(String timeOfDischarge) {
		this.timeOfDischarge = timeOfDischarge;
	}

	public String getTimeOfOperation() {
		return timeOfOperation;
	}

	public void setTimeOfOperation(String timeOfOperation) {
		this.timeOfOperation = timeOfOperation;
	}

	public String getReferenceName() {
		return referenceName;
	}

	public void setReferenceName(String referenceName) {
		this.referenceName = referenceName;
	}

	public String getDischargeStatus() {
		return dischargeStatus;
	}

	public void setDischargeStatus(String dischargeStatus) {
		this.dischargeStatus = dischargeStatus;
	}

	public String getDischargeOutcome() {
		return dischargeOutcome;
	}

	public void setDischargeOutcome(String dischargeOutcome) {
		this.dischargeOutcome = dischargeOutcome;
	}

	public String getBedLog() {
		return bedLog;
	}

	public void setBedLog(String bedLog) {
		this.bedLog = bedLog;
	}

	public String getHospitalCourse() {
		return hospitalCourse;
	}

	public void setHospitalCourse(String hospitalCourse) {
		this.hospitalCourse = hospitalCourse;
	}

}
