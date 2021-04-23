package com.dpdocter.response;

import com.dpdocter.collections.GenericCollection;

public class PreOperationAssessmentResponse extends GenericCollection{

private String id;
	
	private String doctorId;

	private String locationId;

	private String hospitalId;
	
	private String patientId;
	
	private String complaint;

	private String pastHistory;

	private String generalExam;

	private String investigation;

	private String diagnosis;
	
	private String treatmentsPlan;

	private boolean discarded = false;
	
	private String localExam;

	private String ipdNumber;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
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

	public boolean isDiscarded() {
		return discarded;
	}

	public void setDiscarded(boolean discarded) {
		this.discarded = discarded;
	}

	

	public String getLocalExam() {
		return localExam;
	}

	public void setLocalExam(String localExam) {
		this.localExam = localExam;
	}

	public String getIpdNumber() {
		return ipdNumber;
	}

	public void setIpdNumber(String ipdNumber) {
		this.ipdNumber = ipdNumber;
	}

	
	
}
