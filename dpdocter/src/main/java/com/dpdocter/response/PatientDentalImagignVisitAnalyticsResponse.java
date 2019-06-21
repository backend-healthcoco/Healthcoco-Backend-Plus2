package com.dpdocter.response;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.beans.User;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PatientDentalImagignVisitAnalyticsResponse {

	private String doctorName;

	private String doctorId;

	private User doctor;

	private Integer count;

	private Integer visitedCount;

	private Integer paidCount;

	private List<DentalImagingResponse> responses;

	private List<DentalImagingResponse> paidResponses;

	private List<DentalImagingResponse> visitedResponses;

	private Double totalAmount = 0.0;

	private Double paidAmount = 0.0;

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
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

	public Integer getPaidCount() {
		return paidCount;
	}

	public void setPaidCount(Integer paidCount) {
		this.paidCount = paidCount;
	}

	public List<DentalImagingResponse> getPaidResponses() {
		return paidResponses;
	}

	public void setPaidResponses(List<DentalImagingResponse> paidResponses) {
		this.paidResponses = paidResponses;
	}

	public List<DentalImagingResponse> getVisitedResponses() {
		return visitedResponses;
	}

	public void setVisitedResponses(List<DentalImagingResponse> visitedResponses) {
		this.visitedResponses = visitedResponses;
	}

	public List<DentalImagingResponse> getResponses() {
		return responses;
	}

	public void setResponses(List<DentalImagingResponse> responses) {
		this.responses = responses;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Double getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(Double paidAmount) {
		this.paidAmount = paidAmount;
	}

}
