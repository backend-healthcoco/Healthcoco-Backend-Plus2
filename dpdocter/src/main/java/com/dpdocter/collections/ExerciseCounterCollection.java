package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.MealQuantity;
import com.dpdocter.request.ExerciseAddEdit;

@Document(collection = "exercise_counter_cl")
public class ExerciseCounterCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private Date date = new Date();
	@Field
	private List<ExerciseAddEdit> exercises;
	@Field
	private MealQuantity calories;
	@Field
	private Boolean discarded = false;

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

	public List<ExerciseAddEdit> getExercises() {
		return exercises;
	}

	public void setExercises(List<ExerciseAddEdit> exercises) {
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

}
