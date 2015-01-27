package com.dpdocter.request;

public class RecordsSearchRequest {
	private String patientId;
	private String doctorId;
	private String tagId;
	
	
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
	public String getTagId() {
		return tagId;
	}
	public void setTagId(String tagId) {
		this.tagId = tagId;
	}
	@Override
	public String toString() {
		return "RecordsSearchRequest [patientId=" + patientId + ", doctorId="
				+ doctorId + ", tagId=" + tagId + "]";
	}
	
	
	
	
}
