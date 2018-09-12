package com.dpdocter.beans;

import com.dpdocter.enums.MealTimeEnum;
import com.dpdocter.enums.MealType;

public class MealTimeAndPattern {

	private Meal food;

	private MealTimeEnum timeType;

	private int fromTime;

	private int toTime;

	private MealType type;

	public Meal getFood() {
		return food;
	}

	public void setFood(Meal food) {
		this.food = food;
	}

	public MealTimeEnum getTimeType() {
		return timeType;
	}

	public void setTimeType(MealTimeEnum timeType) {
		this.timeType = timeType;
	}

	public int getFromTime() {
		return fromTime;
	}

	public void setFromTime(int fromTime) {
		this.fromTime = fromTime;
	}

	public int getToTime() {
		return toTime;
	}

	public void setToTime(int toTime) {
		this.toTime = toTime;
	}

	public MealType getType() {
		return type;
	}

	public void setType(MealType type) {
		this.type = type;
	}

}
