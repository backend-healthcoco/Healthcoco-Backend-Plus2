package com.dpdocter.request;

public class RecordsSmsRequest {

	private String doctorId;
	private String patientId;
	private String reportId;
	private String message;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ReportSmsRequest [doctorId=" + doctorId + ", patientId=" + patientId + ", reportId=" + reportId
				+ ", message=" + message + "]";
	}

}
