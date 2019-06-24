package com.dpdocter.beans;

import java.util.Map;

import com.dpdocter.collections.GenericCollection;

public class RecipeStep extends GenericCollection {

	private String id;
	private String recipeId;
	private Map<String, RecipeStepsDetails> stepDetails;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRecipeId() {
		return recipeId;
	}

	public void setRecipeId(String recipeId) {
		this.recipeId = recipeId;
	}

	public Map<String, RecipeStepsDetails> getStepDetails() {
		return stepDetails;
	}

	public void setStepDetails(Map<String, RecipeStepsDetails> stepDetails) {
		this.stepDetails = stepDetails;
	}

	@Override
	public String toString() {
		return "RecipeStep [id=" + id + ", recipeId=" + recipeId + ", stepDetails=" + stepDetails + "]";
	}

}
