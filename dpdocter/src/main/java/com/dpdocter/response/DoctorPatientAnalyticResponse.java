package com.dpdocter.response;

public class DoctorPatientAnalyticResponse {

	private Integer totalPatient = 0;
	private Integer totalNewPatient = 0;
	private Integer changeInTotalPatientInPercent = 0;
	private Integer totalVisitedPatient = 0;

	public Integer getChangeInTotalPatientInPercent() {
		return changeInTotalPatientInPercent;
	}

	public void setChangeInTotalPatientInPercent(Integer changeInTotalPatientInPercent) {
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