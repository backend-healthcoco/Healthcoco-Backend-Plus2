package com.dpdocter.response;

import java.util.Date;

import com.dpdocter.collections.GenericCollection;

public class AdmitCardResponseFieldWise extends GenericCollection {

	private Date admissionDate;
	private Date dischargeDate;
	private Date operationDate;
	private String natureOfOperation;
	private String pastHistory;
	private String familyHistory;	
	private String complaint;
	private String xRayDetails;
	private String treatmentsPlan;
	private String diagnosis;
	private String 	examination;
	private String timeOfAdmission;
	private String timeOfDischarge;
	private String timeOfOperation;
	private String preOprationalOrders;
	private String nursingCare;
	private String ipdNumber;
	
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
	public Date getOperationDate() {
		return operationDate;
	}
	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}
	public String getNatureOfOperation() {
		return natureOfOperation;
	}
	public void setNatureOfOperation(String natureOfOperation) {
		this.natureOfOperation = natureOfOperation;
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
	public String getComplaint() {
		return complaint;
	}
	public void setComplaint(String complaint) {
		this.complaint = complaint;
	}
	public String getxRayDetails() {
		return xRayDetails;
	}
	public void setxRayDetails(String xRayDetails) {
		this.xRayDetails = xRayDetails;
	}
	public String getTreatmentsPlan() {
		return treatmentsPlan;
	}
	public void setTreatmentsPlan(String treatmentsPlan) {
		this.treatmentsPlan = treatmentsPlan;
	}
	public String getDiagnosis() {
		return diagnosis;
	}
	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}
	public String getExamination() {
		return examination;
	}
	public void setExamination(String examination) {
		this.examination = examination;
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
	public String getPreOprationalOrders() {
		return preOprationalOrders;
	}
	public void setPreOprationalOrders(String preOprationalOrders) {
		this.preOprationalOrders = preOprationalOrders;
	}
	public String getNursingCare() {
		return nursingCare;
	}
	public void setNursingCare(String nursingCare) {
		this.nursingCare = nursingCare;
	}
	public String getIpdNumber() {
		return ipdNumber;
	}
	public void setIpdNumber(String ipdNumber) {
		this.ipdNumber = ipdNumber;
	}
	
	
}
