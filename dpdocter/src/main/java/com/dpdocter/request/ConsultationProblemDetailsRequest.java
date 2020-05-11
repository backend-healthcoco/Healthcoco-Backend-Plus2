package com.dpdocter.request;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class ConsultationProblemDetailsRequest extends GenericCollection{

	private String id;
	
	private String doctorId;
	
	private String userId;
	
	private List<String>recordId;
	
	private String appointmentId;
	
	private String problemDetail;

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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<String> getRecordId() {
		return recordId;
	}

	public void setRecordId(List<String> recordId) {
		this.recordId = recordId;
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public String getProblemDetail() {
		return problemDetail;
	}

	public void setProblemDetail(String problemDetail) {
		this.problemDetail = problemDetail;
	}
	
	

}
