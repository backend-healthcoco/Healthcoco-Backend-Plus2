package com.dpdocter.request;

import org.bson.types.ObjectId;

import com.dpdocter.beans.Distance;
import com.dpdocter.beans.MealQuantity;

public class ExerciseCounterAddItem {

	private ObjectId id;

	private String name;

	private Integer timeInMinute;

	private Distance distance;

	private MealQuantity calories;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTimeInMinute() {
		return timeInMinute;
	}

	public void setTimeInMinute(Integer timeInMinute) {
		this.timeInMinute = timeInMinute;
	}

	public Distance getDistance() {
		return distance;
	}

	public void setDistance(Distance distance) {
		this.distance = distance;
	}

	public MealQuantity getCalories() {
		return calories;
	}

	public void setCalories(MealQuantity calories) {
		this.calories = calories;
	}

}
