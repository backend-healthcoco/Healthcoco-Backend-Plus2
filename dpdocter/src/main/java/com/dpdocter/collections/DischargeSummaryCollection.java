package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.PrescriptionAndAdvice;
import com.dpdocter.beans.VitalSigns;
import com.dpdocter.beans.WorkingHours;

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
	private List<String> surgeonNames;
	@Field
	private List<String> anesthetistNames;
	@Field
	private String implant;
	@Field
	private String cement;

	public Date getOperationDate() {
		return operationDate;
	}

	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}

	public List<String> getSurgeonNames() {
		return surgeonNames;
	}

	public void setSurgeonNames(List<String> surgeonNames) {
		this.surgeonNames = surgeonNames;
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

	public List<String> getAnesthetistNames() {
		return anesthetistNames;
	}

	public void setAnesthetistNames(List<String> anesthetistNames) {
		this.anesthetistNames = anesthetistNames;
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

}
