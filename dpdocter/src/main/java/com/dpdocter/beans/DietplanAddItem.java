package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.MealTimeEnum;

public class DietplanAddItem {
	private MealTimeEnum mealTiming;

	private List<DietPlanRecipeAddItem> recipes;

	private String note;

	public MealTimeEnum getMealTiming() {
		return mealTiming;
	}

	public void setMealTiming(MealTimeEnum mealTiming) {
		this.mealTiming = mealTiming;
	}

	public List<DietPlanRecipeAddItem> getRecipes() {
		return recipes;
	}

	public void setRecipes(List<DietPlanRecipeAddItem> recipes) {
		this.recipes = recipes;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
