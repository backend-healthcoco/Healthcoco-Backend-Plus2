package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.response.TestAndRecordDataResponse;

public class Prescription extends GenericCollection {
	private String id;

	private String uniqueEmrId;

	private String name;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private List<PrescriptionItemDetail> items;

	private Boolean inHistory = false;

	private Boolean discarded = false;

	private List<TestAndRecordDataResponse> diagnosticTests;

	private String advice;

	private String visitId;

	private String patientId;

	private Boolean isFeedbackAvailable = false;

	private String appointmentId;

	private Appointment appointment;

	private Long nextReview;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PrescriptionItemDetail> getItems() {
		return items;
	}

	public void setItems(List<PrescriptionItemDetail> items) {
		this.items = items;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
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

	public String getAdvice() {
		return advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
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

	public Boolean getInHistory() {
		return inHistory;
	}

	public void setInHistory(Boolean inHistory) {
		this.inHistory = inHistory;
	}

	public Boolean getIsFeedbackAvailable() {
		return isFeedbackAvailable;
	}

	public void setIsFeedbackAvailable(Boolean isFeedbackAvailable) {
		this.isFeedbackAvailable = isFeedbackAvailable;
	}

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
	}

	public List<TestAndRecordDataResponse> getDiagnosticTests() {
		return diagnosticTests;
	}

	public void setDiagnosticTests(List<TestAndRecordDataResponse> diagnosticTests) {
		this.diagnosticTests = diagnosticTests;
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public Appointment getAppointment() {
		return appointment;
	}

	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}

	public Long getNextReview() {
		return nextReview;
	}

	public void setNextReview(Long nextReview) {
		this.nextReview = nextReview;
	}

	@Override
	public String toString() {
		return "Prescription [id=" + id + ", uniqueEmrId=" + uniqueEmrId + ", name=" + name + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", items=" + items + ", inHistory="
				+ inHistory + ", discarded=" + discarded + ", diagnosticTests=" + diagnosticTests + ", advice=" + advice
				+ ", visitId=" + visitId + ", patientId=" + patientId + ", isFeedbackAvailable=" + isFeedbackAvailable
				+ ", appointmentId=" + appointmentId + ", appointment=" + appointment + ", nextReview=" + nextReview
				+ "]";
	}
}
