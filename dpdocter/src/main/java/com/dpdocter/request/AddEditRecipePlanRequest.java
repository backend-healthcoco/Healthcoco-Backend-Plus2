package com.dpdocter.request;

import java.util.List;

public class AddEditRecipePlanRequest {

	private String recipeId;
	private List<String> planIds;

	public String getRecipeId() {
		return recipeId;
	}

	public List<String> getPlanIds() {
		return planIds;
	}

	public void setPlanIds(List<String> planIds) {
		this.planIds = planIds;
	}

	public void setRecipeId(String recipeId) {
		this.recipeId = recipeId;
	}

}
