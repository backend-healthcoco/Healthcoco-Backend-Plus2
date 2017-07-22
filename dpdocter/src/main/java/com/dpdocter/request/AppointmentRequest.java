package com.dpdocter.request;

import java.util.Date;

import com.dpdocter.beans.WorkingHours;
import com.dpdocter.enums.AppointmentCreatedBy;
import com.dpdocter.enums.AppointmentState;

import common.util.web.JacksonUtil;

public class AppointmentRequest {

	private String appointmentId;

	private AppointmentState state;

	private String explanation;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private WorkingHours time;

	private Date fromDate;

	private Date toDate;

	private AppointmentCreatedBy createdBy;

	private Boolean notifyPatientBySms;

	private Boolean notifyPatientByEmail;

	private Boolean notifyDoctorBySms;

	private Boolean notifyDoctorByEmail;

	private String cancelledBy;

	private String localPatientName;

	private String mobileNumber;

	private String visitId;
	
	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public AppointmentState getState() {
		return state;
	}

	public void setState(AppointmentState state) {
		this.state = state;
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

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public WorkingHours getTime() {
		return time;
	}

	public void setTime(WorkingHours time) {
		this.time = time;
	}

	public AppointmentCreatedBy getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(AppointmentCreatedBy createdBy) {
		this.createdBy = createdBy;
	}

	public Boolean getNotifyPatientBySms() {
		return notifyPatientBySms;
	}

	public void setNotifyPatientBySms(Boolean notifyPatientBySms) {
		this.notifyPatientBySms = notifyPatientBySms;
	}

	public Boolean getNotifyPatientByEmail() {
		return notifyPatientByEmail;
	}

	public void setNotifyPatientByEmail(Boolean notifyPatientByEmail) {
		this.notifyPatientByEmail = notifyPatientByEmail;
	}

	public Boolean getNotifyDoctorBySms() {
		return notifyDoctorBySms;
	}

	public void setNotifyDoctorBySms(Boolean notifyDoctorBySms) {
		this.notifyDoctorBySms = notifyDoctorBySms;
	}

	public Boolean getNotifyDoctorByEmail() {
		return notifyDoctorByEmail;
	}

	public void setNotifyDoctorByEmail(Boolean notifyDoctorByEmail) {
		this.notifyDoctorByEmail = notifyDoctorByEmail;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public String getCancelledBy() {
		return cancelledBy;
	}

	public void setCancelledBy(String cancelledBy) {
		this.cancelledBy = cancelledBy;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public String getVisitId() {
		return visitId;
	}

	public void setVisitId(String visitId) {
		this.visitId = visitId;
	}

	@Override
	public String toString() {
		return "AppointmentRequest [appointmentId=" + appointmentId + ", state=" + state + ", explanation="
				+ explanation + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", patientId=" + patientId + ", time=" + time + ", fromDate=" + fromDate + ", toDate=" + toDate
				+ ", createdBy=" + createdBy + ", notifyPatientBySms=" + notifyPatientBySms + ", notifyPatientByEmail="
				+ notifyPatientByEmail + ", notifyDoctorBySms=" + notifyDoctorBySms + ", notifyDoctorByEmail="
				+ notifyDoctorByEmail + ", cancelledBy=" + cancelledBy + ", localPatientName=" + localPatientName
				+ ", mobileNumber=" + mobileNumber + ", visitId=" + visitId + "]";
	}
}
