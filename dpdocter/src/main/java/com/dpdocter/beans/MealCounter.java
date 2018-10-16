package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.MealTimeEnum;
import com.dpdocter.request.RecipeCounterAddItem;

public class MealCounter extends GenericCollection {

	private String id;

	private Date date = new Date();

	private List<RecipeCounterAddItem> recipes;

	private String note;

	private String userId;

	private SimpleCalorie simpleCalorie;

	private MealTimeEnum mealTime = MealTimeEnum.BREAKFAST;

	private MealQuantity calories;

	private MealQuantity fat;

	private MealQuantity protein;

	private MealQuantity carbohydreate;

	private MealQuantity fiber;

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

	public List<RecipeCounterAddItem> getRecipes() {
		return recipes;
	}

	public void setRecipes(List<RecipeCounterAddItem> recipes) {
		this.recipes = recipes;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public SimpleCalorie getSimpleCalorie() {
		return simpleCalorie;
	}

	public void setSimpleCalorie(SimpleCalorie simpleCalorie) {
		this.simpleCalorie = simpleCalorie;
	}

	public MealTimeEnum getMealTime() {
		return mealTime;
	}

	public void setMealTime(MealTimeEnum mealTime) {
		this.mealTime = mealTime;
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
