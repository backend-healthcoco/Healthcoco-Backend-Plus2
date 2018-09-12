package com.dpdocter.beans;

import com.dpdocter.enums.MealTimeEnum;
import com.dpdocter.enums.MealType;
import com.dpdocter.request.MealRequest;

public class MealTimeAndPatternRequest {

	private MealRequest food;

	private MealTimeEnum timeType;

	private int fromTime;

	private int toTime;

	private MealType type;

	public MealType getType() {
		return type;
	}

	public void setType(MealType type) {
		this.type = type;
	}

	public MealRequest getFood() {
		return food;
	}

	public void setFood(MealRequest food) {
		this.food = food;
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

	public MealTimeEnum getTimeType() {
		return timeType;
	}

	public void setTimeType(MealTimeEnum timeType) {
		this.timeType = timeType;
	}

}
