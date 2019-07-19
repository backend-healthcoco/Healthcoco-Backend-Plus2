package com.dpdocter.beans;

public class AppointmentDownloadData {

	private String doctorName;

	private String patientName;

	private String patientId;

	private String date;

	private String startTime;
	
	private String endTime;

	private String status;

	private String explanation;

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	@Override
	public String toString() {
		return "AppointmentDownloadData [doctorName=" + doctorName + ", patientName=" + patientName + ", patientId="
				+ patientId + ", date=" + date + ", startTime=" + startTime + ", endTime=" + endTime + ", status="
				+ status + ", explanation=" + explanation + "]";
	}
}
