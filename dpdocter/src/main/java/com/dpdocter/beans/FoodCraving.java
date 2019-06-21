package com.dpdocter.beans;

import com.dpdocter.enums.MealType;

public class FoodCraving {

	private Meal food;

	private Integer noOfTime = 0;
	
	private MealType type;

	public MealType getType() {
		return type;
	}

	public void setType(MealType type) {
		this.type = type;
	}

	public Meal getFood() {
		return food;
	}

	public void setFood(Meal food) {
		this.food = food;
	}

	public Integer getNoOfTime() {
		return noOfTime;
	}

	public void setNoOfTime(Integer noOfTime) {
		this.noOfTime = noOfTime;
	}

}
