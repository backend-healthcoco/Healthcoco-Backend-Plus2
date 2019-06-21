package com.dpdocter.beans;

public class Exercise {

	private String id;

	private String name;

	private Integer timeInMinute;

	private Distance distance;

	private MealQuantity calories;

	private Boolean discarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

}
