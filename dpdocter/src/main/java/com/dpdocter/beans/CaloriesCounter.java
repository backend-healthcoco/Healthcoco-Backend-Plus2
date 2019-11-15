package com.dpdocter.beans;

import java.util.Date;

import com.dpdocter.collections.GenericCollection;

public class CaloriesCounter extends GenericCollection {
	private String id;

	private Date date = new Date();

	private String userId;

	private MealQuantity calories;

	private MealQuantity fat;

	private MealQuantity protein;

	private MealQuantity carbohydrate;

	private MealQuantity fiber;

	private MealQuantity GoalIntakeCalories;

	private MealQuantity GoalIntakeFat;

	private MealQuantity GoalIntakeProtein;

	private MealQuantity GoalIntakecarbohydrate;

	private MealQuantity GoalIntakeFiber;

	private Boolean discarded = false;

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public MealQuantity getCarbohydrate() {
		return carbohydrate;
	}

	public void setCarbohydrate(MealQuantity carbohydrate) {
		this.carbohydrate = carbohydrate;
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

	public MealQuantity getGoalIntakecarbohydrate() {
		return GoalIntakecarbohydrate;
	}

	public void setGoalIntakecarbohydrate(MealQuantity goalIntakecarbohydrate) {
		GoalIntakecarbohydrate = goalIntakecarbohydrate;
	}

	public MealQuantity getGoalIntakeFiber() {
		return GoalIntakeFiber;
	}

	public void setGoalIntakeFiber(MealQuantity goalIntakeFiber) {
		GoalIntakeFiber = goalIntakeFiber;
	}

}
