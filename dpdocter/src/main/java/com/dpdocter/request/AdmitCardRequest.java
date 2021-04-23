package com.dpdocter.request;

import java.util.Date;

import com.dpdocter.beans.VitalSigns;

public class AdmitCardRequest {

	private String id;
	private String patientId;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private String uniqueEmrId;
	private Date admissionDate;
	private Date dischargeDate;
	private Date operationDate;
	private String natureOfOperation;
	private String pastHistory;
	private String familyHistory;
	private String personalHistory;
	private String complaint;
	private String xRayDetails;
	private String jointInvolvement;
	private String treatmentsPlan;
	private String examination;
	private String diagnosis;
	private Boolean discarded = false;
	private Date createdTime;
	private String timeOfAdmission;
	private String timeOfDischarge;
	private String timeOfOperation;
	private String ip;
	private String address;
	
	private VitalSigns vitalSigns;
	private String preOprationalOrders;
	private String nursingCare;
	private String ipdNumber;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	
	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getExamination() {
		return examination;
	}

	public void setExamination(String examination) {
		this.examination = examination;
	}

	public String getId() {
		return id;
	}

	public String getPatientId() {
		return patientId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public String getLocationId() {
		return locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public Date getAdmissionDate() {
		return admissionDate;
	}

	public Date getDischargeDate() {
		return dischargeDate;
	}

	public Date getOperationDate() {
		return operationDate;
	}

	public String getNatureOfOperation() {
		return natureOfOperation;
	}

	public String getPastHistory() {
		return pastHistory;
	}

	public String getFamilyHistory() {
		return familyHistory;
	}

	public String getPersonalHistory() {
		return personalHistory;
	}

	public String getComplaint() {
		return complaint;
	}

	public String getxRayDetails() {
		return xRayDetails;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}

	public void setDischargeDate(Date dischargeDate) {
		this.dischargeDate = dischargeDate;
	}

	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}

	public void setNatureOfOperation(String natureOfOperation) {
		this.natureOfOperation = natureOfOperation;
	}

	public void setPastHistory(String pastHistory) {
		this.pastHistory = pastHistory;
	}

	public void setFamilyHistory(String familyHistory) {
		this.familyHistory = familyHistory;
	}

	public void setPersonalHistory(String personalHistory) {
		this.personalHistory = personalHistory;
	}

	public void setComplaint(String complaint) {
		this.complaint = complaint;
	}

	public void setxRayDetails(String xRayDetails) {
		this.xRayDetails = xRayDetails;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public String getJointInvolvement() {
		return jointInvolvement;
	}

	public void setJointInvolvement(String jointInvolvement) {
		this.jointInvolvement = jointInvolvement;
	}

	public String getTreatmentsPlan() {
		return treatmentsPlan;
	}

	public void setTreatmentsPlan(String treatmentsPlan) {
		this.treatmentsPlan = treatmentsPlan;
	}

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
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

	
	public VitalSigns getVitalSigns() {
		return vitalSigns;
	}

	public void setVitalSigns(VitalSigns vitalSigns) {
		this.vitalSigns = vitalSigns;
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

	@Override
	public String toString() {
		return "AdmitCardRequest [id=" + id + ", patientId=" + patientId + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", uniqueEmrId=" + uniqueEmrId + ", admissionDate="
				+ admissionDate + ", dischargeDate=" + dischargeDate + ", operationDate=" + operationDate
				+ ", natureOfOperation=" + natureOfOperation + ", pastHistory=" + pastHistory + ", familyHistory="
				+ familyHistory + ", personalHistory=" + personalHistory + ", complaint=" + complaint + ", xRayDetails="
				+ xRayDetails + ", jointInvolvement=" + jointInvolvement + ", treatmentsPlan=" + treatmentsPlan
				+ ", examination=" + examination + ", diagnosis=" + diagnosis + ", discarded=" + discarded
				+ ", createdTime=" + createdTime + ", timeOfAdmission=" + timeOfAdmission + ", timeOfDischarge="
				+ timeOfDischarge + ", timeOfOperation=" + timeOfOperation + "]";
	}

}
