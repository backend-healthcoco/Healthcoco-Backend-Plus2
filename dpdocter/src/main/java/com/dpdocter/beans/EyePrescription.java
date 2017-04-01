package com.dpdocter.beans;

import java.util.Date;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.request.AppointmentRequest;

public class EyePrescription extends GenericCollection {

	private String id;

	private String uniqueEmrId;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private EyeTest leftEyeTest;

	private EyeTest rightEyeTest;

	private String type;

	private Integer pupilaryDistance;

	private String lensType;

	private String usage;

	private String replacementInterval;

	private String lensColor;

	private String lensBrand;

	private String remarks;

	private String prescriptionCode;

	private Boolean inHistory = false;

	private Boolean discarded = false;

	private Boolean isOTPVerified = false;

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

	public EyeTest getLeftEyeTest() {
		return leftEyeTest;
	}

	public void setLeftEyeTest(EyeTest leftEyeTest) {
		this.leftEyeTest = leftEyeTest;
	}

	public EyeTest getRightEyeTest() {
		return rightEyeTest;
	}

	public void setRightEyeTest(EyeTest rightEyeTest) {
		this.rightEyeTest = rightEyeTest;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getPupilaryDistance() {
		return pupilaryDistance;
	}

	public void setPupilaryDistance(Integer pupilaryDistance) {
		this.pupilaryDistance = pupilaryDistance;
	}

	public String getLensType() {
		return lensType;
	}

	public void setLensType(String lensType) {
		this.lensType = lensType;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getPrescriptionCode() {
		return prescriptionCode;
	}

	public void setPrescriptionCode(String prescriptionCode) {
		this.prescriptionCode = prescriptionCode;
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

	public String getReplacementInterval() {
		return replacementInterval;
	}

	public void setReplacementInterval(String replacementInterval) {
		this.replacementInterval = replacementInterval;
	}

	public String getLensColor() {
		return lensColor;
	}

	public void setLensColor(String lensColor) {
		this.lensColor = lensColor;
	}

	public String getLensBrand() {
		return lensBrand;
	}

	public void setLensBrand(String lensBrand) {
		this.lensBrand = lensBrand;
	}

	@Override
	public String toString() {
		return "EyePrescription [id=" + id + ", uniqueEmrId=" + uniqueEmrId + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", leftEyeTest="
				+ leftEyeTest + ", rightEyeTest=" + rightEyeTest + ", type=" + type + ", pupilaryDistance="
				+ pupilaryDistance + ", lensType=" + lensType + ", usage=" + usage + ", replacementInterval="
				+ replacementInterval + ", lensColor=" + lensColor + ", lensBrand=" + lensBrand + ", remarks=" + remarks
				+ ", prescriptionCode=" + prescriptionCode + ", inHistory=" + inHistory + ", discarded=" + discarded
				+ ", isOTPVerified=" + isOTPVerified + ", appointmentId=" + appointmentId + ", time=" + time
				+ ", fromDate=" + fromDate + ", appointmentRequest=" + appointmentRequest + ", visitId=" + visitId
				+ "]";
	}
}
