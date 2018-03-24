package com.dpdocter.response;

public class DoctorPatientAnalyticResponse {

	private Integer totalPatient = 0;
	private Integer totalNewPatient = 0;
	private Double changeInTotalPatientInPercent = 0.0;
	private Integer totalVisitedPatient = 0;

	public Double getChangeInTotalPatientInPercent() {
		return changeInTotalPatientInPercent;
	}

	public void setChangeInTotalPatientInPercent(Double changeInTotalPatientInPercent) {
		this.changeInTotalPatientInPercent = changeInTotalPatientInPercent;
	}

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

	public Integer getTotalVisitedPatient() {
		return totalVisitedPatient;
	}

	public void setTotalVisitedPatient(Integer totalVisitedPatient) {
		this.totalVisitedPatient = totalVisitedPatient;
	}

}
