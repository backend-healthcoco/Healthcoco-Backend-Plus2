package com.dpdocter.response;

public class DoctorTreatmentAnalyticResponse {
	private Integer totalTreatmentService;
	private Integer totalTreatmentServiceCompleted;
	private Integer totalTreatmentServiceProgress;
	private Integer totalTreatmentServiceNotStarted;
	private String  treatmentServiceName;

	public Integer getTotalTreatmentService() {
		return totalTreatmentService;
	}

	public void setTotalTreatmentService(Integer totalTreatmentService) {
		this.totalTreatmentService = totalTreatmentService;
	}

	public Integer getTotalTreatmentServiceCompleted() {
		return totalTreatmentServiceCompleted;
	}

	public void setTotalTreatmentServiceCompleted(Integer totalTreatmentServiceCompleted) {
		this.totalTreatmentServiceCompleted = totalTreatmentServiceCompleted;
	}

	public Integer getTotalTreatmentServiceProgress() {
		return totalTreatmentServiceProgress;
	}

	public void setTotalTreatmentServiceProgress(Integer totalTreatmentServiceProgress) {
		this.totalTreatmentServiceProgress = totalTreatmentServiceProgress;
	}

	public Integer getTotalTreatmentServiceNotStarted() {
		return totalTreatmentServiceNotStarted;
	}

	public void setTotalTreatmentServiceNotStarted(Integer totalTreatmentServiceNotStarted) {
		this.totalTreatmentServiceNotStarted = totalTreatmentServiceNotStarted;
	}

	public String getTreatmentServiceName() {
		return treatmentServiceName;
	}

	public void setTreatmentServiceName(String treatmentServiceName) {
		this.treatmentServiceName = treatmentServiceName;
	}

	@Override
	public String toString() {
		return "DoctorTreatmentAnalyticResponse [totalTreatmentService=" + totalTreatmentService
				+ ", totalTreatmentServiceCompleted=" + totalTreatmentServiceCompleted
				+ ", totalTreatmentServiceProgress=" + totalTreatmentServiceProgress
				+ ", totalTreatmentServiceNotStarted=" + totalTreatmentServiceNotStarted + ", treatmentServiceName="
				+ treatmentServiceName + "]";
	}

}
