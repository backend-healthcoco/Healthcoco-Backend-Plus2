package com.dpdocter.response;

import com.dpdocter.beans.NutritionPlan;

public class NutritionPlanWithCategoryResponse {

	private String category;

	private NutritionPlan nutritionPlan;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public NutritionPlan getNutritionPlan() {
		return nutritionPlan;
	}

	public void setNutritionPlan(NutritionPlan nutritionPlan) {
		this.nutritionPlan = nutritionPlan;
	}

}
