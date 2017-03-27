package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.request.AppointmentRequest;

public class EyePrescription extends GenericCollection {

	private String id;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private List<VisualAcuity> visualAcuities;

	private List<EyeTest> eyeTests;

	private Boolean inHistory = false;

	private Boolean discarded = false;

	private Boolean isOTPVerified = false;

	private String uniqueEmrId;
	
	private String appointmentId;

	private WorkingHours time;

	private Date fromDate;

	private AppointmentRequest appointmentRequest;

	private String visitId;

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

	public List<VisualAcuity> getVisualAcuities() {
		return visualAcuities;
	}

	public void setVisualAcuities(List<VisualAcuity> visualAcuities) {
		this.visualAcuities = visualAcuities;
	}

	public List<EyeTest> getEyeTests() {
		return eyeTests;
	}

	public void setEyeTests(List<EyeTest> eyeTests) {
		this.eyeTests = eyeTests;
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

	public Boolean getIsOTPVerified() {
		return isOTPVerified;
	}

	public void setIsOTPVerified(Boolean isOTPVerified) {
		this.isOTPVerified = isOTPVerified;
	}

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
	}

	public String getVisitId() {
		return visitId;
	}

	public void setVisitId(String visitId) {
		this.visitId = visitId;
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

	public AppointmentRequest getAppointmentRequest() {
		return appointmentRequest;
	}

	public void setAppointmentRequest(AppointmentRequest appointmentRequest) {
		this.appointmentRequest = appointmentRequest;
	}

	@Override
	public String toString() {
		return "EyePrescription [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", patientId=" + patientId + ", visualAcuities=" + visualAcuities + ", eyeTests="
				+ eyeTests + ", inHistory=" + inHistory + ", discarded=" + discarded + ", isOTPVerified="
				+ isOTPVerified + "]";
	}

}
