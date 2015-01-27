package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.Tags;

public class RecordsAddRequest {
	
	private String patientId;
	private String doctorId;
	private Long createdDate;
	
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
	
	
	public Long getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Long createdDate) {
		this.createdDate = createdDate;
	}
	@Override
	public String toString() {
		return "RecordsAddRequest [patientId=" + patientId + ", doctorId="
				+ doctorId + ", createdDate=" + createdDate + "]";
	}
	
}
