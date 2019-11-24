package com.dpdocter.collections;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "admit_card_cl")
public class AdmitCardCollection extends GenericCollection {
	@Field
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
	private Date admissionDate;
	@Field
	private Date dischargeDate;
	@Field
	private Date operationDate;
	@Field
	private String uniqueEmrId;
	@Field
	private String natureOfOperation;
	@Field
	private String pastHistory;
	@Field
	private String familyHistory;
	@Field
	private String personalHistory;
	@Field
	private String complaint;
	@Field
	private String xRayDetails;
	@Field
	private String jointInvolvement;
	@Field
	private String treatmentsPlan;
	@Field
	private String diagnosis;	
	@Field
	private Boolean discarded=false;
	@Field
	private String examination;
	
	@Field
	private Boolean isPatientDiscarded = false;
	
	@Field
	private String timeOfAdmission;
	
	@Field
	private String timeOfDischarge;
	
	@Field
	private String timeOfOperation;
	
	@Field
	private String ip;
	
	@Field
	private String address;
	
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

	
	public String getExamination() {
		return examination;
	}

	public void setExamination(String examination) {
		this.examination = examination;
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

	public ObjectId getPatientId() {
		return patientId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public ObjectId getHospitalId() {
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

	public String getJointInvolvement() {
		return jointInvolvement;
	}

	public String getTreatmentsPlan() {
		return treatmentsPlan;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	

	public void setId(ObjectId id) {
		this.id = id;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public void setHospitalId(ObjectId hospitalId) {
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

	public void setJointInvolvement(String jointInvolvement) {
		this.jointInvolvement = jointInvolvement;
	}

	public void setTreatmentsPlan(String treatmentsPlan) {
		this.treatmentsPlan = treatmentsPlan;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
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

	@Override
	public String toString() {
		return "AdmitCardCollection [id=" + id + ", patientId=" + patientId + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", admissionDate=" + admissionDate + ", dischargeDate="
				+ dischargeDate + ", operationDate=" + operationDate + ", uniqueEmrId=" + uniqueEmrId
				+ ", natureOfOperation=" + natureOfOperation + ", pastHistory=" + pastHistory + ", familyHistory="
				+ familyHistory + ", personalHistory=" + personalHistory + ", complaint=" + complaint + ", xRayDetails="
				+ xRayDetails + ", jointInvolvement=" + jointInvolvement + ", treatmentsPlan=" + treatmentsPlan
				+ ", diagnosis=" + diagnosis + ", discarded=" + discarded + ", examination=" + examination
				+ ", isPatientDiscarded=" + isPatientDiscarded + ", timeOfAdmission=" + timeOfAdmission
				+ ", timeOfDischarge=" + timeOfDischarge + ", timeOfOperation=" + timeOfOperation + "]";
	}

}
