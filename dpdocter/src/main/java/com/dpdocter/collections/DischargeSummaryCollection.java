package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.DoctorAndCost;
import com.dpdocter.beans.FlowSheet;
import com.dpdocter.beans.MonitoringChart;
import com.dpdocter.beans.OTReports;
import com.dpdocter.beans.PrescriptionAndAdvice;
import com.dpdocter.beans.Surgery;
import com.dpdocter.beans.TimeDuration;
import com.dpdocter.beans.VitalSigns;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.beans.v2.PrescriptionItemDetail;
import com.dpdocter.enums.AnaesthesiaTypeEnum;

@Document(collection = "discharge_summary_cl")
public class DischargeSummaryCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private ObjectId patientId;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId prescriptionId;
	@Field
	private String complaints;
	@Field
	private String presentComplaints;
	@Field
	private PrescriptionAndAdvice prescriptions;
	@Field
	private String historyOfPresentComplaints;
	@Field
	private String generalExamination;
	@Field
	private String systemicExamination;
	@Field
	private String dischargeId;
	@Field
	private String appointmentId;
	@Field
	private String complaint;
	@Field
	private String presentComplaint;
	@Field
	private String presentComplaintHistory;
	@Field
	private String generalExam;
	@Field
	private String systemExam;
	@Field
	private String uniqueEmrId;
	@Field
	private String diagnosis;
	@Field
	private String pastHistory;
	@Field
	private String familyHistory;
	@Field
	private String personalHistory;
	@Field
	private String menstrualHistory;
	@Field
	private String obstetricHistory;
	@Field
	private String observation;
	@Field
	private String investigation;
	@Field
	private String pa;
	@Field
	private String ps;
	@Field
	private String pv;
	@Field
	private String echo;
	@Field
	private String xRayDetails;
	@Field
	private String operationNotes;
	@Field
	private String treatmentsGiven;
	@Field
	private WorkingHours time;
	@Field
	private Date fromDate;
	@Field
	private Date admissionDate;
	@Field
	private Date dischargeDate;
	@Field
	private String labourNotes;
	@Field
	private String babyWeight;
	@Field
	private String babyNotes;
	@Field
	private String conditionsAtDischarge;
	@Field
	private String summary;
	@Field
	private Boolean discarded = false;
	@Field
	private String indicationOfUSG;
	@Field
	private String ecgDetails;
	@Field
	private String holter;
	@Field
	private String procedureNote;
	@Field
	private String doctorIncharge;
	@Field
	private VitalSigns vitalSigns;
	@Field
	private Date operationDate;
	@Field
	private String surgeon;
	@Field
	private String anesthetist;
	@Field
	private String implant;
	@Field
	private String cement;
	@Field
	private String operationName;
	@Field
	private String lmp;
	@Field
	private String edd;
	@Field
	private Date surgeryDate;
	@Field
	private String surgeryNotes;
	@Field
	private String treatingConsultant;
	@Field
	private String anesthesia;
	@Field
	private String consultantDoctor;
	@Field
	private String assistantDoctor;
	@Field
	private String treatmentAdviceForMother;
	@Field
	private String treatmentAdviceForBaby;
	@Field
	private String pediatricianName;
	@Field
	private Boolean isPatientDiscarded = false;
	@Field
	private List<FlowSheet> flowSheets;

	@Field
	private List<MonitoringChart> monitoringChart;

	@Field
	private List<String> diagrams;

	@Field
	private String timeOfEntryInOt;

	@Field
	private String timeOfExitFromOt;

	@Field
	private String bloodLoss;

	@Field
	private String ageOnAdmission;

	@Field
	private String ageOnDischarge;

	@Field
	private String weightOnAdmission;

	@Field
	private String weightOnDischarge;

	@Field
	private String timeOfAdmission;

	@Field
	private String timeOfDischarge;

	@Field
	private String timeOfOperation;

	@Field
	private String referenceName;

	@Field
	private String dischargeStatus;

	@Field
	private String dischargeOutcome;

	@Field
	private String bedLog;

	@Field
	private String hospitalCourse;

	// OT fields
	@Field
	private AnaesthesiaTypeEnum anaesthesiaType;
	@Field
	private String provisionalDiagnosis;
	@Field
	private Surgery surgery;
	@Field
	private String finalDiagnosis;
	@Field
	private String operatingSurgeon;
	@Field
	private String anaesthetist;
	@Field
	private List<String> assitingDoctors;
	@Field
	private List<String> assitingNurses;
	@Field
	private Boolean materialForHPE = false;
	@Field
	private TimeDuration timeDuration;
	@Field
	private String remarks;
	@Field
	private DoctorAndCost operatingSurgeonAndCost;
	@Field
	private DoctorAndCost anaesthetistAndCost;
	@Field
	private List<DoctorAndCost> assitingDoctorsAndCost;
	@Field
	private List<DoctorAndCost> assitingNursesAndCost;
	@Field
	private List<PrescriptionItemDetail> postOperativeOrder;

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

	public void setComplaints(String complaints) {
		this.complaints = complaints;
	}

	public void setPresentComplaints(String presentComplaints) {
		this.presentComplaints = presentComplaints;
	}

	public void setPrescriptions(PrescriptionAndAdvice prescriptions) {
		this.prescriptions = prescriptions;
	}

	public void setHistoryOfPresentComplaints(String historyOfPresentComplaints) {
		this.historyOfPresentComplaints = historyOfPresentComplaints;
	}

	public void setGeneralExamination(String generalExamination) {
		this.generalExamination = generalExamination;
	}

	public void setSystemicExamination(String systemicExamination) {
		this.systemicExamination = systemicExamination;
	}

	public void setDischargeId(String dischargeId) {
		this.dischargeId = dischargeId;
	}

	public VitalSigns getVitalSigns() {
		return vitalSigns;
	}

	public void setVitalSigns(VitalSigns vitalSigns) {
		this.vitalSigns = vitalSigns;
	}

	public String getDoctorIncharge() {
		return doctorIncharge;
	}

	public void setDoctorIncharge(String doctorIncharge) {
		this.doctorIncharge = doctorIncharge;
	}

	public String getProcedureNote() {
		return procedureNote;
	}

	public void setProcedureNote(String procedureNote) {
		this.procedureNote = procedureNote;
	}

	public PrescriptionAndAdvice getPrescriptions() {
		return prescriptions;
	}

	public String getComplaints() {
		return complaints;
	}

	public String getPresentComplaints() {
		return presentComplaints;
	}

	public String getHistoryOfPresentComplaints() {
		return historyOfPresentComplaints;
	}

	public String getGeneralExamination() {
		return generalExamination;
	}

	public String getSystemicExamination() {
		return systemicExamination;
	}

	public String setUniqueEmrId() {
		return dischargeId;
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
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

	public ObjectId getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(ObjectId prescriptionId) {
		this.prescriptionId = prescriptionId;
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

	public String getTreatmentsGiven() {
		return treatmentsGiven;
	}

	public void setTreatmentsGiven(String treatmentsGiven) {
		this.treatmentsGiven = treatmentsGiven;
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

	public void setConditionsAtDischarge(String conditionsAtDischarge) {
		this.conditionsAtDischarge = conditionsAtDischarge;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
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

	public String getDischargeId() {
		return dischargeId;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
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

	public List<MonitoringChart> getMonitoringChart() {
		return monitoringChart;
	}

	public void setMonitoringChart(List<MonitoringChart> monitoringChart) {
		this.monitoringChart = monitoringChart;
	}

	public AnaesthesiaTypeEnum getAnaesthesiaType() {
		return anaesthesiaType;
	}

	public void setAnaesthesiaType(AnaesthesiaTypeEnum anaesthesiaType) {
		this.anaesthesiaType = anaesthesiaType;
	}

	public String getProvisionalDiagnosis() {
		return provisionalDiagnosis;
	}

	public void setProvisionalDiagnosis(String provisionalDiagnosis) {
		this.provisionalDiagnosis = provisionalDiagnosis;
	}

	public Surgery getSurgery() {
		return surgery;
	}

	public void setSurgery(Surgery surgery) {
		this.surgery = surgery;
	}

	public String getFinalDiagnosis() {
		return finalDiagnosis;
	}

	public void setFinalDiagnosis(String finalDiagnosis) {
		this.finalDiagnosis = finalDiagnosis;
	}

	public String getOperatingSurgeon() {
		return operatingSurgeon;
	}

	public void setOperatingSurgeon(String operatingSurgeon) {
		this.operatingSurgeon = operatingSurgeon;
	}

	public String getAnaesthetist() {
		return anaesthetist;
	}

	public void setAnaesthetist(String anaesthetist) {
		this.anaesthetist = anaesthetist;
	}

	public List<String> getAssitingDoctors() {
		return assitingDoctors;
	}

	public void setAssitingDoctors(List<String> assitingDoctors) {
		this.assitingDoctors = assitingDoctors;
	}

	public List<String> getAssitingNurses() {
		return assitingNurses;
	}

	public void setAssitingNurses(List<String> assitingNurses) {
		this.assitingNurses = assitingNurses;
	}

	public Boolean getMaterialForHPE() {
		return materialForHPE;
	}

	public void setMaterialForHPE(Boolean materialForHPE) {
		this.materialForHPE = materialForHPE;
	}

	public TimeDuration getTimeDuration() {
		return timeDuration;
	}

	public void setTimeDuration(TimeDuration timeDuration) {
		this.timeDuration = timeDuration;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public DoctorAndCost getOperatingSurgeonAndCost() {
		return operatingSurgeonAndCost;
	}

	public void setOperatingSurgeonAndCost(DoctorAndCost operatingSurgeonAndCost) {
		this.operatingSurgeonAndCost = operatingSurgeonAndCost;
	}

	public DoctorAndCost getAnaesthetistAndCost() {
		return anaesthetistAndCost;
	}

	public void setAnaesthetistAndCost(DoctorAndCost anaesthetistAndCost) {
		this.anaesthetistAndCost = anaesthetistAndCost;
	}

	public List<DoctorAndCost> getAssitingDoctorsAndCost() {
		return assitingDoctorsAndCost;
	}

	public void setAssitingDoctorsAndCost(List<DoctorAndCost> assitingDoctorsAndCost) {
		this.assitingDoctorsAndCost = assitingDoctorsAndCost;
	}

	public List<DoctorAndCost> getAssitingNursesAndCost() {
		return assitingNursesAndCost;
	}

	public void setAssitingNursesAndCost(List<DoctorAndCost> assitingNursesAndCost) {
		this.assitingNursesAndCost = assitingNursesAndCost;
	}

	public List<PrescriptionItemDetail> getPostOperativeOrder() {
		return postOperativeOrder;
	}

	public void setPostOperativeOrder(List<PrescriptionItemDetail> postOperativeOrder) {
		this.postOperativeOrder = postOperativeOrder;
	}

	@Override
	public String toString() {
		return "DischargeSummaryCollection [id=" + id + ", patientId=" + patientId + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", prescriptionId=" + prescriptionId
				+ ", complaints=" + complaints + ", presentComplaints=" + presentComplaints + ", prescriptions="
				+ prescriptions + ", historyOfPresentComplaints=" + historyOfPresentComplaints + ", generalExamination="
				+ generalExamination + ", systemicExamination=" + systemicExamination + ", dischargeId=" + dischargeId
				+ ", appointmentId=" + appointmentId + ", complaint=" + complaint + ", presentComplaint="
				+ presentComplaint + ", presentComplaintHistory=" + presentComplaintHistory + ", generalExam="
				+ generalExam + ", systemExam=" + systemExam + ", uniqueEmrId=" + uniqueEmrId + ", diagnosis="
				+ diagnosis + ", pastHistory=" + pastHistory + ", familyHistory=" + familyHistory + ", personalHistory="
				+ personalHistory + ", menstrualHistory=" + menstrualHistory + ", obstetricHistory=" + obstetricHistory
				+ ", observation=" + observation + ", investigation=" + investigation + ", pa=" + pa + ", ps=" + ps
				+ ", pv=" + pv + ", echo=" + echo + ", xRayDetails=" + xRayDetails + ", operationNotes="
				+ operationNotes + ", treatmentsGiven=" + treatmentsGiven + ", time=" + time + ", fromDate=" + fromDate
				+ ", admissionDate=" + admissionDate + ", dischargeDate=" + dischargeDate + ", labourNotes="
				+ labourNotes + ", babyWeight=" + babyWeight + ", babyNotes=" + babyNotes + ", conditionsAtDischarge="
				+ conditionsAtDischarge + ", summary=" + summary + ", discarded=" + discarded + ", indicationOfUSG="
				+ indicationOfUSG + ", ecgDetails=" + ecgDetails + ", holter=" + holter + ", procedureNote="
				+ procedureNote + ", doctorIncharge=" + doctorIncharge + ", vitalSigns=" + vitalSigns
				+ ", operationDate=" + operationDate + ", surgeon=" + surgeon + ", anesthetist=" + anesthetist
				+ ", implant=" + implant + ", cement=" + cement + ", operationName=" + operationName + ", lmp=" + lmp
				+ ", edd=" + edd + ", surgeryDate=" + surgeryDate + ", surgeryNotes=" + surgeryNotes
				+ ", treatingConsultant=" + treatingConsultant + ", anesthesia=" + anesthesia + ", consultantDoctor="
				+ consultantDoctor + ", assistantDoctor=" + assistantDoctor + ", treatmentAdviceForMother="
				+ treatmentAdviceForMother + ", treatmentAdviceForBaby=" + treatmentAdviceForBaby
				+ ", pediatricianName=" + pediatricianName + ", isPatientDiscarded=" + isPatientDiscarded
				+ ", flowSheets=" + flowSheets + ", diagrams=" + diagrams + ", timeOfEntryInOt=" + timeOfEntryInOt
				+ ", timeOfExitFromOt=" + timeOfExitFromOt + ", bloodLoss=" + bloodLoss + ", ageOnAdmission="
				+ ageOnAdmission + ", ageOnDischarge=" + ageOnDischarge + ", weightOnAdmission=" + weightOnAdmission
				+ ", weightOnDischarge=" + weightOnDischarge + ", timeOfAdmission=" + timeOfAdmission
				+ ", timeOfDischarge=" + timeOfDischarge + ", timeOfOperation=" + timeOfOperation + ", referenceName="
				+ referenceName + ", dischargeStatus=" + dischargeStatus + ", dischargeOutcome=" + dischargeOutcome
				+ ", bedLog=" + bedLog + ", hospitalCourse=" + hospitalCourse + "]";
	}
}
