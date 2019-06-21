package com.dpdocter.collections;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.MealQuantity;

@Document(collection = "calories_counter_cl")
public class CaloriesCounterCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private Date date = new Date();
	@Field
	private ObjectId userId;
	@Field
	private MealQuantity calories;
	@Field
	private MealQuantity fat;
	@Field
	private MealQuantity protein;
	@Field
	private MealQuantity carbohydreate;
	@Field
	private MealQuantity fiber;
	@Field
	private MealQuantity GoalIntakeCalories;
	@Field
	private MealQuantity GoalIntakeFat;
	@Field
	private MealQuantity GoalIntakeProtein;
	@Field
	private MealQuantity GoalIntakeCarbohydreate;
	@Field
	private MealQuantity GoalIntakeFiber;
	@Field
	private Boolean discarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public MealQuantity getCalories() {
		return calories;
	}

	public void setCalories(MealQuantity calories) {
		this.calories = calories;
	}

	public MealQuantity getFat() {
		return fat;
	}

	public void setFat(MealQuantity fat) {
		this.fat = fat;
	}

	public MealQuantity getProtein() {
		return protein;
	}

	public void setProtein(MealQuantity protein) {
		this.protein = protein;
	}

	public MealQuantity getCarbohydreate() {
		return carbohydreate;
	}

	public void setCarbohydreate(MealQuantity carbohydreate) {
		this.carbohydreate = carbohydreate;
	}

	public MealQuantity getFiber() {
		return fiber;
	}

	public void setFiber(MealQuantity fiber) {
		this.fiber = fiber;
	}

	public MealQuantity getGoalIntakeCalories() {
		return GoalIntakeCalories;
	}

	public void setGoalIntakeCalories(MealQuantity goalIntakeCalories) {
		GoalIntakeCalories = goalIntakeCalories;
	}

	public MealQuantity getGoalIntakeFat() {
		return GoalIntakeFat;
	}

	public void setGoalIntakeFat(MealQuantity goalIntakeFat) {
		GoalIntakeFat = goalIntakeFat;
	}

	public MealQuantity getGoalIntakeProtein() {
		return GoalIntakeProtein;
	}

	public void setGoalIntakeProtein(MealQuantity goalIntakeProtein) {
		GoalIntakeProtein = goalIntakeProtein;
	}

	public MealQuantity getGoalIntakeCarbohydreate() {
		return GoalIntakeCarbohydreate;
	}

	public void setGoalIntakeCarbohydreate(MealQuantity goalIntakeCarbohydreate) {
		GoalIntakeCarbohydreate = goalIntakeCarbohydreate;
	}

	public MealQuantity getGoalIntakeFiber() {
		return GoalIntakeFiber;
	}

	public void setGoalIntakeFiber(MealQuantity goalIntakeFiber) {
		GoalIntakeFiber = goalIntakeFiber;
	}

}
