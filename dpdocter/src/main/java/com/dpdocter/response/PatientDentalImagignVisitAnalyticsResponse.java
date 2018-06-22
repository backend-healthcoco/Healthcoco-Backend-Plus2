package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.beans.PatientAnalyticData;
import com.dpdocter.beans.User;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PatientDentalImagignVisitAnalyticsResponse {

	private String doctorName;
	
	private String doctorId;

	private User doctor;

	private Integer count;

	private Integer visitedCount;

	private List<DentalImagingResponse> responses;

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public List<DentalImagingResponse> getResponses() {
		return responses;
	}

	public void setResponses(List<DentalImagingResponse> responses) {
		this.responses = responses;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getVisitedCount() {
		return visitedCount;
	}

	public void setVisitedCount(Integer visitedCount) {
		this.visitedCount = visitedCount;
	}

	public User getDoctor() {
		return doctor;
	}

	public void setDoctor(User doctor) {
		this.doctor = doctor;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}
	

}
