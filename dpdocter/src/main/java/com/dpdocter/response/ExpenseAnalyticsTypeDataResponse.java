package com.dpdocter.response;

public class ExpenseAnalyticsTypeDataResponse {
	public String expenseType;
	public Double cost = 0.0;
	public String getExpenseType() {
		return expenseType;
	}
	public void setExpenseType(String expenseType) {
		this.expenseType = expenseType;
	}
	public Double getCost() {
		return cost;
	}
	public void setCost(Double cost) {
		this.cost = cost;
	}
	
}
