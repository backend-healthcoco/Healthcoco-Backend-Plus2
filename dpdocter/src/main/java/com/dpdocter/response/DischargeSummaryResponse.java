package com.dpdocter.response;

import java.util.Date;

import com.dpdocter.beans.PrescriptionItemAndAdvice;
import com.dpdocter.beans.ReviewDates;
import com.dpdocter.collections.GenericCollection;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DischargeSummaryResponse extends GenericCollection {

	private String id;
	private String patientId;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private PrescriptionItemAndAdvice prescriptions;
	private String diagnosis;
	private String pastHistory;
	private String familyHistory;
	private String personalHistory;
	private String complaints;
	private String presentComplaints;
	private String historyOfPresentComplaints;
	private String menstrualHistory;
	private String obstetricHistory;
	private String generalExamination;
	private String systemicExamination;
	private String observation;
	private String investigation;
	private String pa;
	private String ps;
	private String pv;
	private String echo;
	private String xRayDetails;
	private String operationNotes;
	private String treatmentsGiven;
	private ReviewDates nextReview;
	private String dischargeId;
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

	public PrescriptionItemAndAdvice getPrescriptions() {
		return prescriptions;
	}

	public void setPrescriptions(PrescriptionItemAndAdvice prescriptions) {
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

	public String getGeneralExamination() {
		return generalExamination;
	}

	public void setGeneralExamination(String generalExamination) {
		this.generalExamination = generalExamination;
	}

	public String getSystemicExamination() {
		return systemicExamination;
	}

	public void setSystemicExamination(String systemicExamination) {
		this.systemicExamination = systemicExamination;
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

	public ReviewDates getNextReview() {
		return nextReview;
	}

	public void setNextReview(ReviewDates nextReview) {
		this.nextReview = nextReview;
	}

	public String getDischargeId() {
		return dischargeId;
	}

	public void setDischargeId(String dischargeId) {
		this.dischargeId = dischargeId;
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

	public String getComplaints() {
		return complaints;
	}

	public void setComplaints(String complaints) {
		this.complaints = complaints;
	}

	public String getPresentComplaints() {
		return presentComplaints;
	}

	public void setPresentComplaints(String presentComplaints) {
		this.presentComplaints = presentComplaints;
	}

	public String getHistoryOfPresentComplaints() {
		return historyOfPresentComplaints;
	}

	public void setHistoryOfPresentComplaints(String historyOfPresentComplaints) {
		this.historyOfPresentComplaints = historyOfPresentComplaints;
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

}
