package com.dpdocter.collections;

import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dpdocter.beans.RecipeStepsDetails;

@Document(collection = "recipe_step_cl")
public class RecipeStepCollection extends GenericCollection {

	private ObjectId id;
	private ObjectId recipeId;
	private Map<String, RecipeStepsDetails> stepDetails;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getRecipeId() {
		return recipeId;
	}

	public void setRecipeId(ObjectId recipeId) {
		this.recipeId = recipeId;
	}

	public Map<String, RecipeStepsDetails> getStepDetails() {
		return stepDetails;
	}

	public void setStepDetails(Map<String, RecipeStepsDetails> stepDetails) {
		this.stepDetails = stepDetails;
	}

}
