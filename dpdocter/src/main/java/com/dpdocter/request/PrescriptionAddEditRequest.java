package com.dpdocter.request;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.DiagnosticTest;
import com.dpdocter.beans.PrescriptionAddItem;
import com.dpdocter.beans.WorkingHours;

public class PrescriptionAddEditRequest {
	private String id;

	private String name;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private List<PrescriptionAddItem> items;

	private String patientId;

	private String prescriptionCode;

	private String createdBy;

	private String visitId;

	private List<DiagnosticTest> diagnosticTests;

	private String advice;

	private AppointmentRequest appointmentRequest;

	private String appointmentId;

	private WorkingHours time;

	private Date fromDate;

	private Date createdTime;

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
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

	public List<PrescriptionAddItem> getItems() {
		return items;
	}

	public void setItems(List<PrescriptionAddItem> items) {
		this.items = items;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getPrescriptionCode() {
		return prescriptionCode;
	}

	public void setPrescriptionCode(String prescriptionCode) {
		this.prescriptionCode = prescriptionCode;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getVisitId() {
		return visitId;
	}

	public void setVisitId(String visitId) {
		this.visitId = visitId;
	}

	public List<DiagnosticTest> getDiagnosticTests() {
		return diagnosticTests;
	}

	public void setDiagnosticTests(List<DiagnosticTest> diagnosticTests) {
		this.diagnosticTests = diagnosticTests;
	}

	public String getAdvice() {
		return advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}

	public AppointmentRequest getAppointmentRequest() {
		return appointmentRequest;
	}

	public void setAppointmentRequest(AppointmentRequest appointmentRequest) {
		this.appointmentRequest = appointmentRequest;
	}

	@Override
	public String toString() {
		return "PrescriptionAddEditRequest [id=" + id + ", name=" + name + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", items=" + items + ", patientId=" + patientId
				+ ", prescriptionCode=" + prescriptionCode + ", createdBy=" + createdBy + ", visitId=" + visitId
				+ ", diagnosticTests=" + diagnosticTests + ", advice=" + advice + ", appointmentRequest="
				+ appointmentRequest + ", appointmentId=" + appointmentId + ", time=" + time + ", fromDate=" + fromDate
				+ "]";
	}

}
