package com.dpdocter.response;

public class DoctorTreatmentAnalyticResponse {
	private int totalTreatmentService;
	private int totalTreatmentServiceCompleted;
	private int totalTreatmentServiceProgress;
	private int totalTreatmentServiceNotStarted;
	private String treatmentServiceName;

	public int getTotalTreatmentService() {
		return totalTreatmentService;
	}

	public void setTotalTreatmentService(int totalTreatmentService) {
		this.totalTreatmentService = totalTreatmentService;
	}

	public int getTotalTreatmentServiceCompleted() {
		return totalTreatmentServiceCompleted;
	}

	public void setTotalTreatmentServiceCompleted(int totalTreatmentServiceCompleted) {
		this.totalTreatmentServiceCompleted = totalTreatmentServiceCompleted;
	}

	public int getTotalTreatmentServiceProgress() {
		return totalTreatmentServiceProgress;
	}

	public void setTotalTreatmentServiceProgress(int totalTreatmentServiceProgress) {
		this.totalTreatmentServiceProgress = totalTreatmentServiceProgress;
	}

	public int getTotalTreatmentServiceNotStarted() {
		return totalTreatmentServiceNotStarted;
	}

	public void setTotalTreatmentServiceNotStarted(int totalTreatmentServiceNotStarted) {
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
