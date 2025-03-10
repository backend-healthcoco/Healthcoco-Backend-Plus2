package com.dpdocter.response;

import java.util.List;

public class ExpenseCountResponse {

	private Double cost;

	private List<ExpenseAnalyticsDataResponse> analyticsDataResponse;


	public List<ExpenseAnalyticsDataResponse> getAnalyticsDataResponse() {
		return analyticsDataResponse;
	}

	public void setAnalyticsDataResponse(List<ExpenseAnalyticsDataResponse> analyticsDataResponse) {
		this.analyticsDataResponse = analyticsDataResponse;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

}
