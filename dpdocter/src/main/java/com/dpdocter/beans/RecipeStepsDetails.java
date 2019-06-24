package com.dpdocter.beans;

import java.util.List;

public class RecipeStepsDetails {

	private String imageUrl;
	private String stepInfo;
	private List<RecipeStepIngredients> ingredients;

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getStepInfo() {
		return stepInfo;
	}

	public void setStepInfo(String stepInfo) {
		this.stepInfo = stepInfo;
	}

	public List<RecipeStepIngredients> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<RecipeStepIngredients> ingredients) {
		this.ingredients = ingredients;
	}

}
