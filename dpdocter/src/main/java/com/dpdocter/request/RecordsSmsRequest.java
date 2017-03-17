package com.dpdocter.request;

public class RecordsSmsRequest {

	private String doctorId;
	private String patientId;
	private String recordId;
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

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "RecordsSmsRequest [doctorId=" + doctorId + ", patientId=" + patientId + ", recordId=" + recordId
				+ ", message=" + message + "]";
	}

}
