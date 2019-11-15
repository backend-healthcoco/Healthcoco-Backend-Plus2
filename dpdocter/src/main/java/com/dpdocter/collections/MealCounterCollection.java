package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.MealQuantity;
import com.dpdocter.beans.RecipeCounterItem;
import com.dpdocter.beans.SimpleCalorie;
import com.dpdocter.enums.MealTimeEnum;

@Document(collection = "meal_counter_cl")
public class MealCounterCollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Field
	private Date date = new Date();

	@Field
	private List<RecipeCounterItem> recipes;

	@Field
	private String note;

	@Field
	private ObjectId userId;

	@Field
	private List<SimpleCalorie> simpleCalories;

	@Field
	private MealTimeEnum mealTime = MealTimeEnum.BREAKFAST;

	@Field
	private MealQuantity calories;

	@Field
	private MealQuantity fat;

	@Field
	private MealQuantity protein;

	@Field
	private MealQuantity carbohydrate;

	@Field
	private MealQuantity fiber;

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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<RecipeCounterItem> getRecipes() {
		return recipes;
	}

	public void setRecipes(List<RecipeCounterItem> recipes) {
		this.recipes = recipes;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	

	public List<SimpleCalorie> getSimpleCalories() {
		return simpleCalories;
	}

	public void setSimpleCalories(List<SimpleCalorie> simpleCalories) {
		this.simpleCalories = simpleCalories;
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

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

}
