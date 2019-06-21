package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.MealTimeEnum;
import com.dpdocter.request.MealRequest;

public class MealTimeAndPatternRequest {

	private List<MealRequest> food;

	private MealTimeEnum timeType;

	private int fromTime;

	private int toTime;

	public List<MealRequest> getFood() {
		return food;
	}

	public void setFood(List<MealRequest> food) {
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
