package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.Quantity;
import com.dpdocter.beans.TestAndRecordData;
import com.dpdocter.beans.WorkingHours;

public class OPDPrescriptionResponse {

	private String id;

	private String uniqueEmrId;

	private String name;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private List<OPDPrescriptionItemResponse> items;

	private List<TestAndRecordData> tests;

	private Boolean inHistory = false;

	private Boolean discarded = false;

	private List<OPDDiagnosticTestResponse> diagnosticTests;

	private String advice;

	private String visitId;

	private String patientId;

	private Boolean isFeedbackAvailable = false;

	private String appointmentId;

	private WorkingHours time;

	private Date fromDate;

	private Appointment appointmentRequest;

	private Quantity quantity;

	private String locationName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public List<OPDPrescriptionItemResponse> getItems() {
		return items;
	}

	public void setItems(List<OPDPrescriptionItemResponse> items) {
		this.items = items;
	}

	public List<TestAndRecordData> getTests() {
		return tests;
	}

	public void setTests(List<TestAndRecordData> tests) {
		this.tests = tests;
	}

	public Boolean getInHistory() {
		return inHistory;
	}

	public void setInHistory(Boolean inHistory) {
		this.inHistory = inHistory;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public List<OPDDiagnosticTestResponse> getDiagnosticTests() {
		return diagnosticTests;
	}

	public void setDiagnosticTests(List<OPDDiagnosticTestResponse> diagnosticTests) {
		this.diagnosticTests = diagnosticTests;
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

	public Boolean getIsFeedbackAvailable() {
		return isFeedbackAvailable;
	}

	public void setIsFeedbackAvailable(Boolean isFeedbackAvailable) {
		this.isFeedbackAvailable = isFeedbackAvailable;
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

	public Appointment getAppointmentRequest() {
		return appointmentRequest;
	}

	public void setAppointmentRequest(Appointment appointmentRequest) {
		this.appointmentRequest = appointmentRequest;
	}

	public Quantity getQuantity() {
		return quantity;
	}

	public void setQuantity(Quantity quantity) {
		this.quantity = quantity;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

}
