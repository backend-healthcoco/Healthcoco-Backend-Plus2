package com.dpdocter.elasticsearch.document;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.dpdocter.beans.Distance;
import com.dpdocter.beans.MealQuantity;

@Document(indexName = "exercises_in", type = "exercises")
public class ESExerciseDocument {
	@Id
	private String id;

	@Field(type = FieldType.String)
	private String name;

	@Field(type = FieldType.Integer)
	private Integer timeInMinute;

	@Field(type = FieldType.Nested)
	private Distance distance;

	@Field(type = FieldType.Nested)
	private MealQuantity calories;

	@Field(type = FieldType.Date)
	private Date updatedTime;

	@Field(type = FieldType.Boolean)
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

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

}
