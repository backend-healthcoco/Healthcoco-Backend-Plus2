package com.dpdocter.response.v2;

import java.util.Date;

import com.dpdocter.beans.v2.PatientCard;
import com.dpdocter.collections.GenericCollection;

public class AdmitCardResponse extends GenericCollection {

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
	private String diagnosis;
	private PatientCard patient;
	private Boolean discarded = false;
	private String examination;

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

	public String getUniqueEmrId() {
		return uniqueEmrId;
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

	public String getJointInvolvement() {
		return jointInvolvement;
	}

	public String getTreatmentsPlan() {
		return treatmentsPlan;
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

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
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

	public void setJointInvolvement(String jointInvolvement) {
		this.jointInvolvement = jointInvolvement;
	}

	public void setTreatmentsPlan(String treatmentsPlan) {
		this.treatmentsPlan = treatmentsPlan;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public PatientCard getPatient() {
		return patient;
	}

	public void setPatient(PatientCard patient) {
		this.patient = patient;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getExamination() {
		return examination;
	}

	public void setExamination(String examination) {
		this.examination = examination;
	}

}
