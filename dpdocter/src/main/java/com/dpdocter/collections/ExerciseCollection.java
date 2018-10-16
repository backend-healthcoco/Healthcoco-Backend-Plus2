package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Distance;
import com.dpdocter.beans.MealQuantity;

@Document(collection = "exercise_cl")
public class ExerciseCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String name;

	@Field
	private Integer timeInMinute;

	@Field
	private Distance distance;

	@Field
	private MealQuantity calories;

	@Field
	private Boolean discarded = false;

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

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
