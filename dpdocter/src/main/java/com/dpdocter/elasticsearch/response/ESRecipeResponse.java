package com.dpdocter.elasticsearch.response;

import java.util.List;

import com.dpdocter.beans.EquivalentQuantities;
import com.dpdocter.beans.IngredientAddItem;
import com.dpdocter.beans.MealQuantity;
import com.dpdocter.beans.RecipeAddItem;

public class ESRecipeResponse {

	private String id;

	private String name;

	private MealQuantity quantity;

	private List<RecipeAddItem> includeIngredients;

	private List<RecipeAddItem> excludeIngredients;

	private List<RecipeAddItem> ingredients;

	private List<IngredientAddItem> nutrients;

	private List<EquivalentQuantities> equivalentMeasurements;

	private boolean nutrientValueAtRecipeLevel = false;

	private MealQuantity calaries;

	private List<String> mealTiming;

	private String direction;

	public List<EquivalentQuantities> getEquivalentMeasurements() {
		return equivalentMeasurements;
	}

	public void setEquivalentMeasurements(List<EquivalentQuantities> equivalentMeasurements) {
		this.equivalentMeasurements = equivalentMeasurements;
	}

	public boolean getNutrientValueAtRecipeLevel() {
		return nutrientValueAtRecipeLevel;
	}

	public void setNutrientValueAtRecipeLevel(boolean nutrientValueAtRecipeLevel) {
		this.nutrientValueAtRecipeLevel = nutrientValueAtRecipeLevel;
	}

	public MealQuantity getCalaries() {
		return calaries;
	}

	public void setCalaries(MealQuantity calaries) {
		this.calaries = calaries;
	}

	public List<String> getMealTiming() {
		return mealTiming;
	}

	public void setMealTiming(List<String> mealTiming) {
		this.mealTiming = mealTiming;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MealQuantity getQuantity() {
		return quantity;
	}

	public void setQuantity(MealQuantity quantity) {
		this.quantity = quantity;
	}

	public List<RecipeAddItem> getIncludeIngredients() {
		return includeIngredients;
	}

	public void setIncludeIngredients(List<RecipeAddItem> includeIngredients) {
		this.includeIngredients = includeIngredients;
	}

	public List<RecipeAddItem> getExcludeIngredients() {
		return excludeIngredients;
	}

	public void setExcludeIngredients(List<RecipeAddItem> excludeIngredients) {
		this.excludeIngredients = excludeIngredients;
	}

	public List<RecipeAddItem> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<RecipeAddItem> ingredients) {
		this.ingredients = ingredients;
	}

	public List<IngredientAddItem> getNutrients() {
		return nutrients;
	}

	public void setNutrients(List<IngredientAddItem> nutrients) {
		this.nutrients = nutrients;
	}

}
