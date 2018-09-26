package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.MealTimeEnum;

public class DietplanItem {

	private MealTimeEnum mealTiming;

	private List<DietPlanRecipeItem> recipes;

	private String note;

	private MealQuantity calaries;

	public MealQuantity getCalaries() {
		return calaries;
	}

	public void setCalaries(MealQuantity calaries) {
		this.calaries = calaries;
	}

	public MealTimeEnum getMealTiming() {
		return mealTiming;
	}

	public void setMealTiming(MealTimeEnum mealTiming) {
		this.mealTiming = mealTiming;
	}

	public List<DietPlanRecipeItem> getRecipes() {
		return recipes;
	}

	public void setRecipes(List<DietPlanRecipeItem> recipes) {
		this.recipes = recipes;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
