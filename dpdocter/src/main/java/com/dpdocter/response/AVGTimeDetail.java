package com.dpdocter.response;

public class AVGTimeDetail {

	private Integer countAppointment = 0;

	private Double avgTreatmentTime = 0.0;

	private Double avgWaitingTime = 0.0;

	private Double avgEngageTime = 0.0;

	public Integer getCountAppointment() {
		return countAppointment;
	}

	public void setCountAppointment(Integer countAppointment) {
		this.countAppointment = countAppointment;
	}

	public Double getAvgTreatmentTime() {
		return avgTreatmentTime;
	}

	public void setAvgTreatmentTime(Double avgTreatmentTime) {
		this.avgTreatmentTime = avgTreatmentTime;
	}

	public Double getAvgWaitingTime() {
		return avgWaitingTime;
	}

	public void setAvgWaitingTime(Double avgWaitingTime) {
		this.avgWaitingTime = avgWaitingTime;
	}

	public Double getAvgEngageTime() {
		return avgEngageTime;
	}

	public void setAvgEngageTime(Double avgEngageTime) {
		this.avgEngageTime = avgEngageTime;
	}

}
