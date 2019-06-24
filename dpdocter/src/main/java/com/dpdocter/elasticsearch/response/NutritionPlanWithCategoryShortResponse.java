package com.dpdocter.elasticsearch.response;

import java.util.List;

import com.dpdocter.response.NutritionPlanShortResponse;

public class NutritionPlanWithCategoryShortResponse {

	private String category;

	private List<NutritionPlanShortResponse> nutritionPlan;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<NutritionPlanShortResponse> getNutritionPlan() {
		return nutritionPlan;
	}

	public void setNutritionPlan(List<NutritionPlanShortResponse> nutritionPlan) {
		this.nutritionPlan = nutritionPlan;
	}

}
