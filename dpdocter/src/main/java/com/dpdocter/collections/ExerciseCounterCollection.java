package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.MealQuantity;
import com.dpdocter.beans.SimpleCalorie;
import com.dpdocter.request.ExerciseCounterAddItem;

@Document(collection = "exercise_counter_cl")
public class ExerciseCounterCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private Date date = new Date();
	@Field
	private List<ExerciseCounterAddItem> exercises;
	@Field
	private MealQuantity calories;
	@Field
	private Boolean discarded = false;
	@Field
	private List<SimpleCalorie> simpleCalories;

	@Field
	private ObjectId userId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<ExerciseCounterAddItem> getExercises() {
		return exercises;
	}

	public void setExercises(List<ExerciseCounterAddItem> exercises) {
		this.exercises = exercises;
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

	public List<SimpleCalorie> getSimpleCalories() {
		return simpleCalories;
	}

	public void setSimpleCalories(List<SimpleCalorie> simpleCalories) {
		this.simpleCalories = simpleCalories;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

}
