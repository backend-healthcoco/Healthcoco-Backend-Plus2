package com.dpdocter.request;

public class FoodCravingRequest {

	private MealRequest food;

	private Integer noOfTime = 0;

	public MealRequest getFood() {
		return food;
	}

	public void setFood(MealRequest food) {
		this.food = food;
	}

	public Integer getNoOfTime() {
		return noOfTime;
	}

	public void setNoOfTime(Integer noOfTime) {
		this.noOfTime = noOfTime;
	}

}
