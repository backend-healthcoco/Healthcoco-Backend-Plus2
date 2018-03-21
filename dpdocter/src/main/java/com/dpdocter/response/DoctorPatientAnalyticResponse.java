package com.dpdocter.response;

public class DoctorPatientAnalyticResponse {

	private Integer totalPatient = 0;
	private Integer totalNewPatient = 0;
	private Integer totalPatientIncrease = 0;
	private Integer totalPatientdecrease = 0;
	private Integer totalVisitedPatient = 0;

	public Integer getTotalPatient() {
		return totalPatient;
	}

	public void setTotalPatient(Integer totalPatient) {
		this.totalPatient = totalPatient;
	}

	public Integer getTotalNewPatient() {
		return totalNewPatient;
	}

	public void setTotalNewPatient(Integer totalNewPatient) {
		this.totalNewPatient = totalNewPatient;
	}

	public Integer getTotalPatientIncrease() {
		return totalPatientIncrease;
	}

	public void setTotalPatientIncrease(Integer totalPatientIncrease) {
		this.totalPatientIncrease = totalPatientIncrease;
	}

	public Integer getTotalPatientdecrease() {
		return totalPatientdecrease;
	}

	public void setTotalPatientdecrease(Integer totalPatientdecrease) {
		this.totalPatientdecrease = totalPatientdecrease;
	}

	public Integer getTotalVisitedPatient() {
		return totalVisitedPatient;
	}

	public void setTotalVisitedPatient(Integer totalVisitedPatient) {
		this.totalVisitedPatient = totalVisitedPatient;
	}

}
