package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection ="pre_operation_form_cl")
public class PreOperationFormCollection extends GenericCollection {

	@Id
    private ObjectId id;
	
	@Field
	private ObjectId doctorId;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;
	
	@Field
	private ObjectId patientId;
	
	@Field
	private String complaint;

	@Field
	private String pastHistory;

	@Field
	private String generalExam;

	@Field
	private String investigation;

	@Field
	private String diagnosis;
	
	@Field
	private String treatmentsPlan;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public String getComplaint() {
		return complaint;
	}

	public void setComplaint(String complaint) {
		this.complaint = complaint;
	}

	public String getPastHistory() {
		return pastHistory;
	}

	public void setPastHistory(String pastHistory) {
		this.pastHistory = pastHistory;
	}

	public String getGeneralExam() {
		return generalExam;
	}

	public void setGeneralExam(String generalExam) {
		this.generalExam = generalExam;
	}

	public String getInvestigation() {
		return investigation;
	}

	public void setInvestigation(String investigation) {
		this.investigation = investigation;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public String getTreatmentsPlan() {
		return treatmentsPlan;
	}

	public void setTreatmentsPlan(String treatmentsPlan) {
		this.treatmentsPlan = treatmentsPlan;
	}

	
	
	
}
