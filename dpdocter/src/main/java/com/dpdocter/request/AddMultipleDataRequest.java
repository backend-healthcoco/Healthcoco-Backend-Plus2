package com.dpdocter.request;

import java.util.Date;

public class AddMultipleDataRequest {

	private String visitId;

	private String patientId;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private ClinicalNotesAddRequest clinicalNote;

	private PrescriptionAddEditRequest prescription;

	private RecordsAddRequest record;

	private AppointmentRequest appointmentRequest;

	private PatientTreatmentAddEditRequest treatmentRequest;

	private Date createdTime;

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getVisitId() {
		return visitId;
	}

	public void setVisitId(String visitId) {
		this.visitId = visitId;
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

	public ClinicalNotesAddRequest getClinicalNote() {
		return clinicalNote;
	}

	public void setClinicalNote(ClinicalNotesAddRequest clinicalNote) {
		this.clinicalNote = clinicalNote;
	}

	public PrescriptionAddEditRequest getPrescription() {
		return prescription;
	}

	public void setPrescription(PrescriptionAddEditRequest prescription) {
		this.prescription = prescription;
	}

	public RecordsAddRequest getRecord() {
		return record;
	}

	public void setRecord(RecordsAddRequest record) {
		this.record = record;
	}

	public AppointmentRequest getAppointmentRequest() {
		return appointmentRequest;
	}

	public void setAppointmentRequest(AppointmentRequest appointmentRequest) {
		this.appointmentRequest = appointmentRequest;
	}

	public PatientTreatmentAddEditRequest getTreatmentRequest() {
		return treatmentRequest;
	}

	public void setTreatmentRequest(PatientTreatmentAddEditRequest treatmentRequest) {
		this.treatmentRequest = treatmentRequest;
	}

	@Override
	public String toString() {
		return "AddMultipleDataRequest [visitId=" + visitId + ", patientId=" + patientId + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", clinicalNote=" + clinicalNote
				+ ", prescription=" + prescription + ", record=" + record + ", appointmentRequest=" + appointmentRequest
				+ "]";
	}

}
