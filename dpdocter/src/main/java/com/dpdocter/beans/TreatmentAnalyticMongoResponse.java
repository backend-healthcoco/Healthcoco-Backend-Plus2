package com.dpdocter.beans;

import java.util.List;

public class TreatmentAnalyticMongoResponse {
	private int totalTreatmentService;
	private List<String>totalTreatmentServiceCompleted;
	private List<String> totalTreatmentServiceProgress;
	private List<String> totalTreatmentServiceNotStarted;
	private String treatmentServiceName;
	public int getTotalTreatmentService() {
		return totalTreatmentService;
	}
	
	public List<String> getTotalTreatmentServiceCompleted() {
		return totalTreatmentServiceCompleted;
	}

	public void setTotalTreatmentServiceCompleted(List<String> totalTreatmentServiceCompleted) {
		this.totalTreatmentServiceCompleted = totalTreatmentServiceCompleted;
	}

	public List<String> getTotalTreatmentServiceProgress() {
		return totalTreatmentServiceProgress;
	}

	public void setTotalTreatmentServiceProgress(List<String> totalTreatmentServiceProgress) {
		this.totalTreatmentServiceProgress = totalTreatmentServiceProgress;
	}

	public List<String> getTotalTreatmentServiceNotStarted() {
		return totalTreatmentServiceNotStarted;
	}

	public void setTotalTreatmentServiceNotStarted(List<String> totalTreatmentServiceNotStarted) {
		this.totalTreatmentServiceNotStarted = totalTreatmentServiceNotStarted;
	}

	public void setTotalTreatmentService(int totalTreatmentService) {
		this.totalTreatmentService = totalTreatmentService;
	}

	public String getTreatmentServiceName() {
		return treatmentServiceName;
	}
	public void setTreatmentServiceName(String treatmentServiceName) {
		this.treatmentServiceName = treatmentServiceName;
	}
	

}