package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.NutritionPlan;

public class NutritionPlanWithCategoryResponse {

	private String category;

	private List<NutritionPlan> nutritionPlan;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<NutritionPlan> getNutritionPlan() {
		return nutritionPlan;
	}

	public void setNutritionPlan(List<NutritionPlan> nutritionPlan) {
		this.nutritionPlan = nutritionPlan;
	}

}
