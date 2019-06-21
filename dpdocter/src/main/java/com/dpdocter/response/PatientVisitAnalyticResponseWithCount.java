package com.dpdocter.response;

import java.util.List;

public class PatientVisitAnalyticResponseWithCount {

	private List<AnalyticResponse> responses;

	private Integer totalCost = 0;

	public List<AnalyticResponse> getResponses() {
		return responses;
	}

	public void setResponses(List<AnalyticResponse> responses) {
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
