package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class ExerciseCounter extends GenericCollection {

	private String id;

	private Date date = new Date();

	private List<Exercise> exercises;

	private MealQuantity calories;

	private String userId;
	
	private List<SimpleCalorie> simpleCalories;


	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<Exercise> getExercises() {
		return exercises;
	}

	public void setExercises(List<Exercise> exercises) {
		this.exercises = exercises;
	}

	public MealQuantity getCalories() {
		return calories;
	}

	public void setCalories(MealQuantity calories) {
		this.calories = calories;
	}

	public List<SimpleCalorie> getSimpleCalories() {
		return simpleCalories;
	}

	public void setSimpleCalories(List<SimpleCalorie> simpleCalories) {
		this.simpleCalories = simpleCalories;
	}

}
