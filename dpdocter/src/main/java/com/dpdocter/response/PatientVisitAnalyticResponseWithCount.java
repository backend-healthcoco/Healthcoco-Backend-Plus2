package com.dpdocter.response;

import java.util.List;

public class PatientVisitAnalyticResponseWithCount {

	private List<PatientAnalyticResponse> responses;

	private Integer totalCost = 0;

	public List<PatientAnalyticResponse> getResponses() {
		return responses;
	}

	public void setResponses(List<PatientAnalyticResponse> responses) {
		this.responses = responses;
	}

	public Integer getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(Integer totalCost) {
		this.totalCost = totalCost;
	}

	@Override
	public String toString() {
		return "PatientVisitAnalyticResponseWithCount [responses=" + responses + ", totalCost=" + totalCost + "]";
	}

}
